package com.tgc.researchchat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ChatAdapterRecycler extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int LATEST_TYPE_MESSAGE_SENT = 3;
    private static final int LATEST_TYPE_MESSAGE_RECEIVED = 4;
    private Context context;
    private ArrayList<Message> arrayList;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    ChatAdapterRecycler(Context context, ArrayList<Message> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = arrayList.get(position);

        if (message.isSent() && position != arrayList.size() - 1)
            return VIEW_TYPE_MESSAGE_SENT;
        else if (!message.isSent() && position != arrayList.size() - 1)
            return VIEW_TYPE_MESSAGE_RECEIVED;
        else if (message.isSent() && position == arrayList.size() - 1)
            return LATEST_TYPE_MESSAGE_SENT;
        else if (!message.isSent() && position == arrayList.size() - 1)
            return LATEST_TYPE_MESSAGE_RECEIVED;
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_bottom);

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == LATEST_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            view.startAnimation(animation);
            return new SentMessageHolder(view);
        } else if (viewType == LATEST_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            view.startAnimation(animation);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = arrayList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case LATEST_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case LATEST_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage, playButton, pauseButton;
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            messageImage = itemView.findViewById(R.id.received_image);
            playButton = itemView.findViewById(R.id.play_button);
            pauseButton = itemView.findViewById(R.id.pause_button);
            itemView.setOnClickListener(this::onClick);
            itemView.setOnLongClickListener(this::onLongclick);
        }


        void bind(Message message) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String currentDateTimeString = sdf.format(message.getTime());

            messageText.setText(message.getMessage());
            timeText.setText(currentDateTimeString);
            Log.d(TAG, "bind: " + message.getMessage());
            if (message.getMessage().contains("New File Received: ") &&
                    (message.getMessage().contains("png") ||
                            message.getMessage().contains("jpg") ||
                            message.getMessage().contains("jpeg"))) {
                String[] fileName = message.getMessage().split(":");
                Log.d(TAG, "bind: Received Message" + fileName[1]);
                StringBuilder stringBuilder = new StringBuilder(fileName[1]);
                stringBuilder.deleteCharAt(0);
                String path = stringBuilder.toString();
                String directory = Environment.getExternalStorageDirectory() + "/Download/" + path;
                Log.d(TAG, "bind: Print Directory:" + directory);
                File imgFile = new File(directory);
                if (imgFile.exists()) {
                    Log.d(TAG, "bind: +Yeah Exists");
                    Glide.with(context)
                            .load(directory)
                            .override(500, 500)
                            .into(messageImage);
                }
            } else if (message.getMessage().contains("New File Received: ") &&
                    (message.getMessage().contains("mp3"))) {
                if (mediaPlayer.isPlaying()) {
                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                } else {
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        void onClick(View view) {
            if (messageText.getText().toString().contains(".mp3") && messageText.getText().toString().contains("New File Received: ")) {
                String[] message = messageText.getText().toString().split(":");
                Log.d(TAG, "onClick: " + message[1]);
                String filename = message[1];
                filename = filename.trim();
                String path = Environment.getExternalStorageDirectory() + "/Download/";
                System.out.println(TAG + "path and filename => " + path+filename);
                Uri uri = Uri.parse(path + filename);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.INVISIBLE);
                } else {
                    mediaPlayer = MediaPlayer.create(context, uri);
                    //Log.d(TAG, "onClick: " + context.getObbDir() + "/downloadFolder/" + path);
                    mediaPlayer.start();
                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                }
            }
        }

        boolean onLongclick(View view) {
            ClipData clip = ClipData.newPlainText("Copied Text", messageText.getText());
            clipboard.setPrimaryClip(clip);
            return true;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage, playButton, pauseButton;
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.send_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            messageImage = itemView.findViewById(R.id.sent_image);
            playButton = itemView.findViewById(R.id.sent_play_button);
            pauseButton = itemView.findViewById(R.id.sent_pause_button);
            itemView.setOnClickListener(this::onClick);
            itemView.setOnLongClickListener(this::onLongclick);
        }

        void bind(Message message) {
            String newMessage = message.getMessage();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String currentDateTimeString = sdf.format(message.getTime());

            if (message.getMessage().contains("New File Sent: ") &&
                    (message.getMessage().contains("png") ||
                            message.getMessage().contains("jpg") ||
                            message.getMessage().contains("jpeg"))) {
                String[] fileName = message.getMessage().split(":");
                Log.d(TAG, "bind: Received Message" + fileName[2]);
                String[] newStringArr = newMessage.split(":");
                newMessage = newStringArr[0] + newStringArr[1];
                Log.d(TAG, "bind: Directory:" + newMessage);
                String directory = newStringArr[2];
                Log.d(TAG, "bind: Print Directory:" + directory);
                File imgFile = new File(directory);
                if (imgFile.exists()) {
                    Log.d(TAG, "bind: +Yeah Exists");
                    Glide.with(context)
                            .load(directory)
                            .override(500, 500)
                            .into(messageImage);
                }
            } else if (message.getMessage().contains("New File Sent: ") &&
                    (message.getMessage().contains("mp3"))) {
                if (mediaPlayer.isPlaying()) {
                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                } else {
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.INVISIBLE);
                }
            }
            messageText.setText(newMessage);
            timeText.setText(currentDateTimeString);
        }

        void onClick(View view) {
            if (messageText.getText().toString().contains(".mp3") && messageText.getText().toString().contains("New File Sent: ")) {
                String[] message = messageText.getText().toString().split(":");
                Log.d(TAG, "onClick: " + message[2]);
                String path = message[2];
                path = path.trim();
                Uri uri = Uri.parse(path);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.INVISIBLE);
                } else {
                    mediaPlayer = MediaPlayer.create(context, uri);
                    mediaPlayer.start();
                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                }
            }
        }

        boolean onLongclick(View view) {
            ClipData clip = ClipData.newPlainText("Copied Text", messageText.getText());
            clipboard.setPrimaryClip(clip);
            return true;
        }
    }
}
