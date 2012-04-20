package com.aquariusinteractive.exception

/**
 *
 * @version
 * @author Michael Bishop
 * 
 */
class ReconcileException extends AppException {

  ReconcileException() {
  }

  ReconcileException(String s) {
    super(s)
  }

  ReconcileException(String s, Throwable throwable) {
    super(s,
          throwable)
  }

  ReconcileException(Throwable throwable) {
    super(throwable)
  }
}
