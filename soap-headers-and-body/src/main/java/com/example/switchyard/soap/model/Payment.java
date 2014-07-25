package com.example.switchyard.soap.model;


public interface Payment {

    public TransferResponse transferFunds(TransferRequest payload);
}
