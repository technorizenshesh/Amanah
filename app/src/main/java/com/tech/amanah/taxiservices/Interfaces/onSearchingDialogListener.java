package com.tech.amanah.taxiservices.Interfaces;


import com.tech.amanah.taxiservices.ModelCurrentBooking;

public interface onSearchingDialogListener {
    void onRequestAccepted(ModelCurrentBooking data);
    void onRequestCancel();
    void onDriverNotFound();
}
