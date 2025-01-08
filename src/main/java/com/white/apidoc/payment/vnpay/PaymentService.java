package com.white.apidoc.payment.vnpay;

import com.white.apidoc.core.config.payment.VNPAYConfig;
import com.white.apidoc.model.Transaction;
import com.white.apidoc.model.User;
import com.white.apidoc.repository.TransactionRepository;
import com.white.apidoc.repository.UserRepository;
import com.white.apidoc.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
       createTransaction(1L, amount, bankCode, "PENDING");

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    public void createTransaction(Long userId, Long amount, String bankCode, String status) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setBankCode(bankCode);
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }
    @Transactional
    public void updateUserBalance(Long userId, Long amount) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found")
        );
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }
}
