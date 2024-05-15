package com.example.demo.order.telementry

import io.opentelemetry.api.trace.Span

class TraceHolder {

    companion object {

        fun currentTraceId() : String {
            val spanContext = Span.current().spanContext
            return spanContext.traceId;
        }

    }

}