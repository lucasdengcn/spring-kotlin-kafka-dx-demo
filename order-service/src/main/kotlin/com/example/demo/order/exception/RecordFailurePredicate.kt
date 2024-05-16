package com.example.demo.order.exception

import java.util.function.Predicate

class RecordFailurePredicate : Predicate<Throwable> {
    override fun test(t: Throwable): Boolean {
        return t !is BusinessException
    }
}
