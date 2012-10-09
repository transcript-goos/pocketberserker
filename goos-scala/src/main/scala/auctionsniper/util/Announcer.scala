package auctionsniper.util

import java.util.EventListener
import java.lang.reflect.{InvocationTargetException, Method, InvocationHandler, Proxy}
import collection.mutable.ArrayBuffer

object Announcer {
  def to[T <: EventListener](implicit m: Manifest[T]) = new Announcer[T]
}

class Announcer[T <: EventListener](implicit m: Manifest[T]) {
  require(m.erasure.isInterface)

  private val listeners = new ArrayBuffer[T]

  private val proxy: T = {
    val listenerType = m.erasure
    Proxy.newProxyInstance(listenerType.getClassLoader, Array(listenerType),
      new InvocationHandler {
        def invoke(proxy: Any, method: Method, args: Array[AnyRef]) = {
          announce(method, args)
          null
        }
      }
    ).asInstanceOf[T]
  }

  def +=(listener: T) {
    listeners += listener
  }

  def -=(listener: T) {
    listeners -= listener
  }

  def announce() : T = proxy

  def announce(m: Method, args: Array[AnyRef]) {
    try {
      listeners.foreach(m.invoke(_, args:_*))
    } catch {
      case e: IllegalAccessException => throw new IllegalArgumentException("could not invoke listener", e)
      case e: InvocationTargetException =>
        e.getCause match {
          case cause: RuntimeException => throw cause
          case cause:  Error => throw cause
          case cause => throw new UnsupportedOperationException("listener threw exception", cause)
        }
    }
  }
}

