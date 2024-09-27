package com.example.demo.streaming.controller

import com.example.demo.streaming.controller.SseEmitterController.Companion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/streaming")
class BodyStreamingController {

    companion object {
        val log: Logger = LoggerFactory.getLogger(BodyStreamingController::class.java)
    }

    val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(5)

    @GetMapping("/srb")
    @ResponseStatus(HttpStatus.OK)
    fun handle() : ResponseEntity<StreamingResponseBody> {
        log.info("Receive SRB request")
        val stream: StreamingResponseBody = StreamingResponseBody {
            // asynchronous response
            executor.scheduleAtFixedRate(Runnable {
                val msg = "/SRB@" + LocalDateTime.now();
                it.write(msg.toByteArray());
                log.info("schedule streaming event.")
            }, 0, 1, TimeUnit.SECONDS);
        }
        return ResponseEntity<StreamingResponseBody>(stream, HttpStatus.OK)
    }

}