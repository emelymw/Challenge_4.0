package de.stihl.challenge_40;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends AppCompatActivity {


    static final UUID espUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter btAdapter_MSA = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice esp_MSA = btAdapter_MSA.getRemoteDevice("94:B9:7E:6B:68:6E");
    BluetoothSocket btSocket_MSA = null;

    private final AtomicBoolean runningMSAThread = new AtomicBoolean(false);

    public String Drehzahl = "";
    private TextView DrehzahlText;
    public String MaxDrehzahl = "";
    private TextView MaxDrehzahlText;
    public String Leistung = "";
    private TextView LeistungText;
    public String Temperatur = "";
    private TextView TemperaturText;
    public String Overheating = "";
    private TextView OverheatingText;
    public String Modus = "";
    private TextView ModusText;
    public String Batteriestand = "";
    private TextView Batterietext;
    public String Gashebel = "";
    private TextView GashebelText;
    public String Bremse = "";
    private TextView BremseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MSAThread msaThread = new MSAThread();
        msaThread.start();
        Log.i("Create", "MSA thread started");
    }

    class MSAThread extends Thread {
        MSAThread() {

        }

        @Override
        public void run() {
            runningMSAThread.set(true);
            Log.i("THREAD", "MSA thread started");

            DrehzahlText = findViewById(R.id.drehzahl);
            MaxDrehzahlText = findViewById(R.id.drehzahlmax);
            LeistungText = findViewById(R.id.leistung);
            TemperaturText = findViewById(R.id.temperatur);
            OverheatingText = findViewById(R.id.overheating);
            ModusText = findViewById(R.id.modus);
            Batterietext = findViewById(R.id.batterie);
            GashebelText = findViewById(R.id.gashebel);
            BremseText = findViewById(R.id.bremse);


            while (runningMSAThread.get()) {
                int counter_bt = 0;
                int bitCount = 0;
                String byteString;
                String temp = "";


                try {
                    btSocket_MSA = esp_MSA.createRfcommSocketToServiceRecord(espUUID);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    btSocket_MSA.connect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                InputStream inputStream;
                try {
                    inputStream = btSocket_MSA.getInputStream();
                    inputStream.skip(inputStream.available());

                    do {
                        byte b = (byte) inputStream.read();
                        byteString = ((char) (b & 0xFF)) + ""; // Byte received via BT

                        if (byteString.equals("S")) {
                            bitCount = 0;
                            temp = "";
                        }
                        //if byte is numeric or ";"
                        if (byteString.matches("[+-]?\\d*(\\.\\d+)?") || byteString.equals(";")) {
                            switch (bitCount) {
                                case 0:
                                    if (byteString.equals(";")) {
                                        Drehzahl = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 1:
                                    if (byteString.equals(";")) {
                                        MaxDrehzahl = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 2:
                                    if (byteString.equals(";")) {
                                        Leistung = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 3:
                                    if (byteString.equals(";")) {
                                        Temperatur = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 4:
                                    if (byteString.equals(";")) {
                                        Overheating = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 5:
                                    if (byteString.equals(";")) {
                                        Modus = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 6:
                                    if (byteString.equals(";")) {
                                        Batteriestand = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 7:
                                    if (byteString.equals(";")) {
                                        Gashebel = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;

                                case 8:
                                    if (byteString.equals(";")) {
                                        Bremse = temp;
                                        temp = "";
                                        bitCount++;
                                    } else temp += byteString;
                                    break;
                            }
                            Log.i("Drehzahl", Drehzahl);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DrehzahlText.setText(Drehzahl);
                                MaxDrehzahlText.setText(MaxDrehzahl);
                                LeistungText.setText(Leistung);
                                TemperaturText.setText(Temperatur);
                                OverheatingText.setText(Overheating);
                                ModusText.setText(Modus);
                                Batterietext.setText(Batteriestand);
                                GashebelText.setText(Gashebel);
                                BremseText.setText(Bremse);
                            }
                        });

                    } while (runningMSAThread.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    btSocket_MSA.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}