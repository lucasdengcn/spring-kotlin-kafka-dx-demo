package com.example.demo.streaming.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/streaming")
class SseEmitterController {

    companion object {
        val log: Logger = LoggerFactory.getLogger(SseEmitterController::class.java)
    }

    val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)
    val emitterMap: ConcurrentHashMap<String, SseEmitter> = ConcurrentHashMap<String, SseEmitter>()
    val userIds: ArrayList<String> = ArrayList<String>()

    @GetMapping("/sse/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun income(@PathVariable userId : String) : SseEmitter {
        log.info("Receive SSE request")
        val emitter0 = createSseEmitter(userId)
        // send event to client
        executor.scheduleAtFixedRate( Runnable {
            val id = userIds.random()
            val emitter = emitterMap[id]
            try {
                emitter?.send(id + " /sse pong@" + LocalDateTime.now(), MediaType.TEXT_PLAIN);
                // complete function will close connection
                // emitter.complete();
                log.info("schedule send event: {}", id)
            } catch (e: Exception) {
                emitter?.completeWithError(e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        return emitter0;
    }

    private fun createSseEmitter(userId: String): SseEmitter {
        val emitter = SseEmitter();
        emitter.onError {
            emitterMap.remove(userId)
            log.info("{} error. {}", emitter, userId)
        }
        emitter.onCompletion {
            emitterMap.remove(userId)
            log.info("{} complete. {}", emitter, userId)
        }
        emitter.onTimeout {
            emitterMap.remove(userId)
            log.info("{} timeout. {}", emitter, userId)
        }
        // link to a specific user for later usage.
        emitterMap[userId] = emitter
        userIds.add(userId)
        return emitter
    }


}