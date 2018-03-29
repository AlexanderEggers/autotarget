package org.autointent.util

import javax.annotation.processing.Filer

interface HelperProcessor {
    fun process(filer: Filer)
}
