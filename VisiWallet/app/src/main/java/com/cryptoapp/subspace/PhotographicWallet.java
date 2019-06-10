package com.cryptoapp.subspace;

import android.net.Uri;

import org.bitcoinj.wallet.Wallet;

import java.io.File;

/**
 * Created by DestinationX on 10/8/2018.
 */

public class PhotographicWallet {
    private File imagePath;
    private File walletPath;
    private Uri uriImagePath;
    private Wallet wallet;


    public PhotographicWallet(Wallet wallet, Uri uriImagePath) {
        this.uriImagePath = uriImagePath;
        this.wallet = wallet;
    }

    public PhotographicWallet(File walletPath, Uri uriImagePath) {
        this.walletPath = walletPath;
        this.uriImagePath = uriImagePath;
    }

    public PhotographicWallet(File imagePath, File walletPath) {
        this.imagePath = imagePath;
        this.walletPath = walletPath;
    }

    //TODO: Getters and setters
    public File getImagePath() {
        return imagePath;
    }

    public void setImagePath(File imagePath) {
        this.imagePath = imagePath;
    }

    public File getWalletPath() {
        return walletPath;
    }

    public void setWalletPath(File walletPath) {
        this.walletPath = walletPath;
    }

    public Uri getUriImagePath() {
        return uriImagePath;
    }

    public void setUriImagePath(Uri uriImagePath) {
        this.uriImagePath = uriImagePath;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
