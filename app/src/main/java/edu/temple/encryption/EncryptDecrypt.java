package edu.temple.encryption;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.R.attr.id;

public class EncryptDecrypt extends AppCompatActivity {
    private Keys keysProvider;
    private String privKey, pubKey;
    private Key thePrivKey, thePubKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_decrypt);
        keysProvider = new Keys();
    }

    @Override
    protected void onStart() {
        super.onStart();



        final Button b = (Button)findViewById(R.id.keyButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.setText("Keys Added");
                Object[] row = new Object[2];
                KeyPairGenerator keyGen;
                try {
                    keyGen = KeyPairGenerator.getInstance("RSA");
                    KeyPair keys = keyGen.generateKeyPair();
                    row[0] = ((RSAPrivateKey)keys.getPrivate()).getPrivateExponent();
                    row[1] = ((RSAPublicKey)keys.getPublic()).getPublicExponent();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                ContentValues vals = new ContentValues();
                vals.put("privateKey", row[0].toString());
                vals.put("publicKey", row[1].toString());
                Uri uri = Uri.parse("thisapp.Keys");
                keysProvider.setSharedPrefs(getApplicationContext().getSharedPreferences("prefs", MODE_PRIVATE));
                keysProvider.insert(uri, vals);
                Cursor cursor = keysProvider.query(uri, null, null, null, null);
                cursor.moveToNext();
                privKey = cursor.getString(cursor.getColumnIndex("privateKey"));
                pubKey = cursor.getString(cursor.getColumnIndex("publicKey"));



                byte[] byteKey = Base64.decode(pubKey.getBytes(), Base64.DEFAULT);
                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                KeyFactory kf = null;

                try {
                    kf = KeyFactory.getInstance("RSA");

                    thePubKey = kf.generatePublic(X509publicKey);

                    byteKey = Base64.decode(privKey.getBytes(), Base64.DEFAULT);
                    X509EncodedKeySpec X509privateKey = new X509EncodedKeySpec(byteKey);
                    kf = KeyFactory.getInstance("RSA");

                    thePrivKey = kf.generatePublic(X509privateKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        Button encryptButton = (Button)findViewById(R.id.encryptButton);
        Button decryptButton = (Button)findViewById(R.id.decryptButton);
        final EditText text = (EditText)findViewById(R.id.message);
        final Cipher c, c2;

        try{


            c = Cipher.getInstance("RSA/NONE/NoPadding");
            c2 = Cipher.getInstance("RSA/NONE/NoPadding");





        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] encryptedBytes;
                try {
                    c.init(Cipher.ENCRYPT_MODE, thePubKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                try {
                    encryptedBytes = c.doFinal(text.getText().toString().getBytes(StandardCharsets.UTF_8));
                    text.setText(Base64.encodeToString(encryptedBytes, Base64.DEFAULT));
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }


            }
        });
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] encryptedBytes;
                try {
                    c2.init(Cipher.DECRYPT_MODE, thePrivKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                try {
                    encryptedBytes = c2.doFinal(Base64.decode(text.getText().toString(), Base64.DEFAULT));
                    text.setText(Base64.encodeToString(encryptedBytes, Base64.DEFAULT));
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            }
        });

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
