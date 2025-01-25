package com.atm.model.dtos.payloads.records.responses;

public record ReceiptData(
        String senderFullName,
        String receiverFullName,
        String senderAccountNumber,
        String receiverAccountNumber,
        String senderAccountType,
        String receiverAccountType,
        String transactionType,
        String date,
        String qrCode,
        String currency,
        double amount,
        double fees,
        double totalAmount
) {
}
