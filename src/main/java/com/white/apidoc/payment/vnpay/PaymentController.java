package com.white.apidoc.payment.vnpay;

import com.white.apidoc.core.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("${spring.application.api-prefix}/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("/")
    public String paymentPage() {
        return "payment"; // Giao diện nhập số tiền và mã ngân hàng
    }
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        PaymentDTO.VNPayResponse response = paymentService.createVnPayPayment(request);
        return new ResponseObject<>(HttpStatus.OK, "Success", response);
    }
    @GetMapping("/vn-pay-callback")
    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        Long userId = 1L; // Thay thế bằng logic lấy userId từ session hoặc token
        Long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100; // VNPay gửi amount * 100
        if (status.equals("00")) {
            paymentService.updateUserBalance(userId, amount);
            return new ResponseObject<>(HttpStatus.OK, "Success", new PaymentDTO.VNPayResponse("00", "Success", ""));
        } else {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
        }
    }
}
