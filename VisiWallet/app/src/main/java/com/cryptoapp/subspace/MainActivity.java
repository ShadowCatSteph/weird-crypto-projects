package com.cryptoapp.subspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;

import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NetworkParameters params = MainNetParams.get();
    WalletAppKit kit;
    private String state;
    private int walletCounter;
    DeterministicSeed seed;
    private List<Wallet> pWalletList = new ArrayList<>();
    private List<File> pFiles = new ArrayList<>();
    MyPreferences myPreferences;
    private static final int WALLET_FILE_DEFAULT_VALUE = 0;
    FloatingActionButton fab;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final Long earliestKeyCreationTime = 1538352000L;
    private File photoDirectory;
    private File walletDirectory;
    private HashMap<File, File> pWalletMap = new HashMap<>();
    File photoFile;
    private List<PhotographicWallet> photographicWalletList = new ArrayList<>();
    private PhotographicWallet photographicWallet;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();

    }

    // TODO: LOAD Views and listeners on creation
    private void setup() {
        fab = findViewById(R.id.fab);
        fab.setImageResource(android.R.drawable.ic_input_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get photo from gallery
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_CHOOSE_PHOTO);
            }
        });
        photoDirectory = new File(getFilesDir(), "pictures");
        walletDirectory = new File(getFilesDir(), "wallets");
        if (!photoDirectory.exists()) {
            System.out.println("setup(): Photo directory does not exist. Creating....");
            photoDirectory.mkdirs();
            System.out.println("setup(): Done....");
        }
        if (!walletDirectory.exists()) {
            System.out.println("setup(): Wallet Directory does not exist. Creating....");
            walletDirectory.mkdirs();
            System.out.println("setup(): Done....");
        }
        // recyclerview

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        // recyclerview layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(this, photographicWalletList);


        loadWalletCounter();
        loadPhotographicWallets();
        recyclerView.setAdapter(adapter);

    }

    // TODO: GET Wallet Counter
    private void loadWalletCounter() {
        walletCounter = MyPreferences.getWalletCounter(this, MyPreferences.WALLET_FILE, WALLET_FILE_DEFAULT_VALUE);
        System.out.println("getWalletCounter(): You have " + walletCounter + " wallets");
    }

    // TODO: SET Wallet Counter
    private void setWalletCounter() {
        MyPreferences.setWalletCounter(this, MyPreferences.WALLET_FILE, walletCounter);
        System.out.println("setWalletCounter(): You now have " + walletCounter + " wallet(s)");
    }

    private void loadPhotographicWallets() {

        if(walletCounter == 0) {

            System.out.println("loadPhotographicWallets(): No photographic wallet list found.");
            System.out.println("loadPhotographicWallets(): Initiating new list...");
            photographicWalletList = new ArrayList<>();
            System.out.println("loadPhotographicWallets(): Done!");

        } else {

            System.out.println("loadPhotographicWallets(): Multiple photographic wallets detected!");
            System.out.println("loadPhotographicWallets(): Retrieving them now....");



            File f = new File(String.valueOf(photoDirectory));
            File file[] = f.listFiles();
            System.out.println("loadPhotographicWallets(): walletCounter = " + walletCounter + " , " + "photodirectory size = " + file.length
            );
            for(int i = 0; i < file.length; i++) {
                System.out.println(getUriFromImageFile(file[i]).toString());
                try {
                    Wallet wallet = Wallet.loadFromFile(new File(walletDirectory + "/wallet" + i + ".wallet"));
                    Uri uri = getUriFromImageFile(file[i]);

                    photographicWalletList.add(new PhotographicWallet(wallet, uri));
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    System.out.println("loadPhotographicWallets(): This entry reads " + photographicWalletList.get(i).getWallet().toString()
                    + "on file " + photographicWalletList.get(i).getUriImagePath());
                    System.out.println("loadWalletsFromFile(): Wallet #" + i + " has " + photographicWalletList.get(i).getWallet().getBalance() + " spendable satoshis");
                    System.out.println("loadWalletsFromFile(): Current receive address = " + photographicWalletList.get(i).getWallet().currentReceiveAddress().toString());
                } catch (UnreadableWalletException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("loadPhotographicWallets(): There are " + walletCounter + " photographic wallets in your collection");
            System.out.println("loadPhotographicWallets(): " + photographicWalletList.toString());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            System.out.println("loadPhotographicWallets(): " + adapter.getItemCount());
        }
    }

    // TODO: LOAD Wallets from File
    private void loadWalletsFromFile() {
//        System.out.println("loadWalletsFromFile(): Loading wallets....");
//        try {
//            state = "onLoad";
//            for (int i = 0; i < walletCounter; i++) {
//
//                //TODO: need some way to bind wallet with picture - SOLVED with hash map
//                //Bitmap picture = picList[i];
//                //pWalletList.add( new PhotographicWallet(picture, wallet))
//
//                System.out.println("loadWalletsFromFile(): Wallet #" + (i+1) + " has " + wallet.getBalance() + " spendable satoshis");
//                Wallet wallet1 = pWalletList.get(i);
//                System.out.println("loadWalletsFromFile(): Wallet " + (i+1) + " current receive address = " + wallet1.currentReceiveAddress().toString());
//            }
//        } catch (UnreadableWalletException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("loadWalletsFromFile(): There are " + pWalletList.size() + " wallets currently loaded");
    }

    // TODO: onActivityForResult callback for selected image
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == REQUEST_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    //get data from callback
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    //create file that stores bitmap image
                    photoFile = null;
                    try {
                        photoFile = createImageFile();

                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if(photoFile != null) {
                        saveImageToFile(selectedImage, photoFile);
                        loadImageFromFile(photoFile);


                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("no image was selected. Backing out...");
            }
        }
    }

    // TODO: Create image file
    private File createImageFile() throws IOException {

        System.out.println("createImageFile(): Creating file...");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String timeStamp = earliestKeyCreationTime.toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = photoDirectory;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        System.out.println("createImageFile(): Success!");

        System.out.println("createImageFile(): You now have " + photographicWalletList.size() + " file(s) in your private image directory");
        for(int i=0; i < photographicWalletList.size(); i++) {
            System.out.println("createImageFile(): Path to image file #" + i + " = " + photographicWalletList.get(i).getUriImagePath().toString());
        }
        return image;
    }

    // TODO: Save/copy Image to File location
    private void saveImageToFile(Bitmap image, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(getBytesFromBitmap(image));
            out.close();
            System.out.println("saveImageToFile(): image saved to file... " + file.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: Loads an image from a given file path
    private void loadImageFromFile(File file) {

        Uri imageUri = Uri.fromFile(file);
        try {
            System.out.println("loadImageFromFile(): Loading image from file...." + imageUri.toString());
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    // TODO: Loads an Uri address from a given file
    private Uri getUriFromImageFile(File file) {
        System.out.println("getUriFromImageFile(): getting Uri address from file....");
        Uri imageUri = Uri.fromFile(file);
        System.out.println("getUriFromImageFile(): Success! File exists at " + imageUri.toString());
            return imageUri;
    }


    // TODO: Get byte array from Bitmap image
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        System.out.println("getBytesFromBitmap(): Initiating compression...");

        System.out.println("getBytesFromBitmap(): Success!");
        return stream.toByteArray();

    }


    // TODO: Create Wallet with filepath and image digest
    private void createWallet(File file, String image) {
        final Uri imageUri = Uri.fromFile(file);

        try {
            System.out.println("createWallet(): Loading image from file...." + imageUri.toString());
            InputStream imageStream = getContentResolver().openInputStream(imageUri);

            seed = new DeterministicSeed(seedBytes, "", earliestKeyCreationTime);
            System.out.println("createWallet(): Seed phrase = " + Joiner.on(" ").join(seed.getMnemonicCode()));
            kit = new WalletAppKit(params, walletDirectory,  "wallet" + walletCounter) {
                @Override
                protected void onSetupCompleted() {
                    wallet().allowSpendingUnconfirmedTransactions();
                    System.out.println("onSetupCompleted(): Setting up wallet listeners...");
                    setWalletListeners(wallet());
                    System.out.println("onSetupCompleted(): Done!");
                    System.out.println("onSetupCompleted(): Mapping photo to wallet file location...");
                    pWalletMap.put(photoFile, new File(walletDirectory + "/wallet" + walletCounter + ".wallet"));
                    System.out.println("onSetupCompleted(): Done!");
                    System.out.println("onSetupCompleted(): Retrieving map entries...");
                    System.out.println("onSetupCompleted(): Wallet map.............. == " + pWalletMap.toString());
                    walletCounter++;
                    setWalletCounter();
                    photographicWalletList.add(new PhotographicWallet(wallet(), imageUri));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                    System.out.println("onSetupCompleted(): Photographic wallet list size = " + photographicWalletList.size());
                }
            };
            kit.restoreWalletFromSeed(seed).setCheckpoints(getAssets()
                    .open("checkpoints.txt"))
                    .setDownloadListener(bListener)
                    .setBlockingStartup(false)
                    .setAutoSave(true)
                    .startAsync();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    // TODO: Set up wallet listeners after setup = complete
    private void setWalletListeners(Wallet wallet) {
        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("onCoinsReceived(): Coins received. Your new balance is " + newBalance + " satoshis");
            }
        });
        wallet.addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {

            }
        });
    }

    // TODO: Blockchain Download Progress Tracker
    DownloadProgressTracker bListener = new DownloadProgressTracker() {
        @Override
        public void doneDownload() {
            System.out.println("doneDownload(): wallet info " + kit.wallet().toString());

            System.out.println("doneDownload(): Wallet map.............. == " + pWalletMap.toString());

        }

        @Override
        protected void progress(double pct, int blocksSoFar, Date date) {
            super.progress(pct, blocksSoFar, date);
            System.out.println("progress" + blocksSoFar);
            System.out.println("Percent done" + pct);

        }

        @Override
        public void onChainDownloadStarted(Peer peer, int blocksLeft) {
            super.onChainDownloadStarted(peer, blocksLeft);
            System.out.println("onChainDownloadStarted(): " + blocksLeft);
        }

        @Override
        public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
            super.onBlocksDownloaded(peer, block, filteredBlock, blocksLeft);
            System.out.println("onBlocksDownloaded " + blocksLeft);

            if(blocksLeft == 0) {

                System.out.println("onBlocksDownloaded(): Wallet map.............. == " + pWalletMap.toString());
            }
            }


        @Override
        protected void startDownload(int blocks) {
            super.startDownload(blocks);
            System.out.println("startDownload");
        }
    };


}

