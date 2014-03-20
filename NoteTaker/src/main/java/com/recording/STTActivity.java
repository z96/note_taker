package com.recording;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.notetaker.R;

import java.util.ArrayList;

/**
 * Created by Dylan on 3/17/14.
 */
public class STTActivity extends Activity {

    private boolean recording = false;
    private boolean recorder_alive = false;
    private SpeechRecognizer recognizer;
    private Intent speech_intent;
    private Intent monitor_intent;
    private RecognizerReceiver receiver;

    private TextView output;

    public class RecognizerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "restartRecognizer") {
                Log.d("stt", "Restarting recognizer...");
                recognizer.startListening(speech_intent);
            }
        }
    }

    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);

        setContentView(R.layout.stt_layout);
        output = (TextView) findViewById(R.id.recorded_text);

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(listener);

        speech_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speech_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speech_intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        //TODO Create monitor
        monitor_intent = new Intent(this, STTService.class);
        startService(monitor_intent);

        IntentFilter filter = new IntentFilter();
        filter.addAction("restartRecognizer");
        receiver = new RecognizerReceiver();
        registerReceiver(receiver, filter);

    }

    public void RecordToggle(View view) {
        Button b = (Button) view;
        if (!recording) {
            b.setText("Stop Recording");

            Intent i = new Intent();
            i.setAction("recordToggle");
            sendBroadcast(i);

            recognizer.startListening(speech_intent);

            recording = !recording;
        }
        else {
            b.setText("Start Recording");

            recognizer.stopListening();

            recording = !recording;
        }
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onResults(Bundle bundle) {
            if (bundle != null) {

                ArrayList<String> out = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                output.setText(output.getText() + " " + out.get(0));
            }

            Intent i = new Intent();
            i.setAction("resultReceived");
            sendBroadcast(i);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    public void onStop() {
        super.onStop();
        //TODO Stop service
        stopService(monitor_intent);
    }
}
