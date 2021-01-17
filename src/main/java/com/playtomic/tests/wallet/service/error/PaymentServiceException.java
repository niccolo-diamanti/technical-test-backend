package com.playtomic.tests.wallet.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Payment refused")
public class PaymentServiceException extends Exception {
}
