package com.example.demo.streaming.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Callable

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/streaming")
class BodyEmitterController {

    companion object {
        val log: Logger = LoggerFactory.getLogger(BodyEmitterController::class.java)
    }

    val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)

    @GetMapping("/rbe")
    @ResponseStatus(HttpStatus.OK)
    fun handle() : ResponseBodyEmitter {
        log.info("Receive RBE request")
        val emitter = ResponseBodyEmitter();
        // asynchronous response
        executor.scheduleAtFixedRate(Runnable {
            try {
                emitter.send("/rbe pong@" + LocalDateTime.now(), MediaType.TEXT_PLAIN);
                // emitter.complete();
            } catch (e: Exception) {
                emitter.completeWithError(e);
            }
        }, 0,1, TimeUnit.SECONDS)

        return emitter;
    }

}