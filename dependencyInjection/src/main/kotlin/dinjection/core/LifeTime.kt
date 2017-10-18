package dinjection.core
import java.io.Closeable

interface LifeTime : AutoCloseable{
    fun addCloseable(t : AutoCloseable)
}