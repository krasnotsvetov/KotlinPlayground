package dinjection.core

import java.io.Closeable

class PrimitiveLifeTime : LifeTime {
    private val toClose = mutableListOf<AutoCloseable>()

    override fun addCloseable(t: AutoCloseable) {
        toClose.add(t);
    }

    override fun close() {
        val curException:Exception? = null;
        for (m in toClose) {
            try {
                m.close();
            } catch (e : Exception) {
                if (curException != null) {
                    curException.addSuppressed(e)
                }
            }
        }
        if (curException != null) {
            throw curException;
        }
    }
}