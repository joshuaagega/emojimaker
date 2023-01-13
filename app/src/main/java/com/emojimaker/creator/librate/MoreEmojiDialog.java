package com.emojimaker.creator.librate;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emojimaker.creator.R;


public class MoreEmojiDialog {
    private ImageView btnCacncel;
    private TextView btnMaybe;
    private TextView btnSubmit;
    private Context context;
    private Dialog dialog;
    SharedPreferences.Editor edit;
    private boolean isPlay;
    private Activity mActivity;
    private String mDescription;
    private boolean mIsFinish;
    MoreEmojiDialogInterFace mMoreEmojiDialogListener;
    private String mTitle;
    private RelativeLayout main;
    SharedPreferences pre;
    private ImageView ratingFace;
    private TextView tvDes;
    private TextView tvTitle;
    private boolean isEnable = true;
    private int defRating = 0;

    
    public interface MoreEmojiDialogInterFace {
        void maybe();

        void ok(boolean z);
    }

    public MoreEmojiDialog(Context context) {
        this.context = context;
        this.pre = context.getSharedPreferences("rateData", 0);
        this.edit = this.pre.edit();
        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(1);
        this.dialog.setContentView(R.layout.dialog_more_emoji);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.btnCacncel = (ImageView) this.dialog.findViewById(R.id.btnCacncel);
        this.ratingFace = (ImageView) this.dialog.findViewById(R.id.ratingFace);
        this.main = (RelativeLayout) this.dialog.findViewById(R.id.main);
        this.btnSubmit = (TextView) this.dialog.findViewById(R.id.btnSubmit);
        this.btnMaybe = (TextView) this.dialog.findViewById(R.id.btnMaybe);
        this.tvTitle = (TextView) this.dialog.findViewById(R.id.tvTitle);
        this.tvDes = (TextView) this.dialog.findViewById(R.id.tvDes);
        this.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { 
            @Override 
            public void onDismiss(DialogInterface dialogInterface) {
                MoreEmojiDialog.this.main.setRotation(0.0f);
                MoreEmojiDialog.this.main.setAlpha(0.0f);
                MoreEmojiDialog.this.main.setScaleY(0.0f);
                MoreEmojiDialog.this.main.setScaleX(0.0f);
                MoreEmojiDialog.this.main.clearAnimation();
                if (MoreEmojiDialog.this.mMoreEmojiDialogListener != null) {
                    MoreEmojiDialog.this.mMoreEmojiDialogListener.maybe();
                }
            }
        });
        this.btnCacncel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MoreEmojiDialog.this.closeDialog();
            }
        });
        this.btnSubmit.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MoreEmojiDialog.this.main.animate().scaleY(0.0f).scaleX(0.0f).alpha(0.0f).rotation(-1800.0f).setDuration(600).setListener(new Animator.AnimatorListener() { 
                    @Override 
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override 
                    public void onAnimationRepeat(Animator animator) {
                    }

                    @Override 
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override 
                    public void onAnimationEnd(Animator animator) {
                        MoreEmojiDialog.this.dialog.dismiss();
                        MoreEmojiDialog.this.main.clearAnimation();
                        if (MoreEmojiDialog.this.mMoreEmojiDialogListener != null) {
                            MoreEmojiDialog.this.mMoreEmojiDialogListener.ok(MoreEmojiDialog.this.isPlay);
                        }
                    }
                }).start();
            }
        });
        this.btnMaybe.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MoreEmojiDialog.this.dialog.dismiss();
                MoreEmojiDialog.this.main.clearAnimation();
                if (MoreEmojiDialog.this.mIsFinish) {
                    MoreEmojiDialog.this.mActivity.finish();
                }
                if (MoreEmojiDialog.this.mMoreEmojiDialogListener != null) {
                    MoreEmojiDialog.this.mMoreEmojiDialogListener.maybe();
                }
            }
        });
    }

    public void changeTitle(String str, String str2) {
        this.mTitle = str;
        this.mDescription = str2;
        this.tvDes.setText(this.mDescription);
        this.tvTitle.setText(this.mTitle);
    }

    public void hideButton(String str) {
        this.btnSubmit.setText(str);
        this.btnMaybe.setVisibility(View.GONE);
    }

    public void showButton(String str) {
        this.btnSubmit.setText(str);
        this.btnMaybe.setVisibility(View.VISIBLE);
    }

    public void CommentOnGooglePlay(boolean z) {
        this.isPlay = z;
    }

    public void showDialog(boolean z, Activity activity) {
        this.mIsFinish = z;
        this.mActivity = activity;
        this.isEnable = this.pre.getBoolean("enb", true);
        if (this.isEnable) {
            this.dialog.show();
            setRatingFace(true);
            this.main.animate().scaleY(1.0f).scaleX(1.0f).rotation(1800.0f).alpha(1.0f).setDuration(600).setListener(new Animator.AnimatorListener() { 
                @Override 
                public void onAnimationCancel(Animator animator) {
                }

                @Override 
                public void onAnimationRepeat(Animator animator) {
                }

                @Override 
                public void onAnimationStart(Animator animator) {
                }

                @Override 
                public void onAnimationEnd(Animator animator) {
                    MoreEmojiDialog.this.main.clearAnimation();
                }
            }).start();
        }
    }

    public void setEnable(boolean z) {
        this.edit.putBoolean("enb", z);
        this.edit.commit();
    }

    public boolean getEnable() {
        return this.pre.getBoolean("enb", true);
    }

    private void setRatingFace(boolean z) {
        if (z) {
            this.ratingFace.setImageResource(R.drawable.favorite);
        } else {
            this.ratingFace.setImageResource(R.drawable.favorite2);
        }
    }

    public void closeDialog() {
        this.main.animate().scaleY(0.0f).scaleX(0.0f).alpha(0.0f).rotation(-1800.0f).setDuration(600).setListener(new Animator.AnimatorListener() { 
            @Override 
            public void onAnimationCancel(Animator animator) {
            }

            @Override 
            public void onAnimationRepeat(Animator animator) {
            }

            @Override 
            public void onAnimationStart(Animator animator) {
            }

            @Override 
            public void onAnimationEnd(Animator animator) {
                MoreEmojiDialog.this.dialog.dismiss();
                MoreEmojiDialog.this.main.clearAnimation();
                if (MoreEmojiDialog.this.mMoreEmojiDialogListener != null) {
                    MoreEmojiDialog.this.mMoreEmojiDialogListener.maybe();
                }
            }
        }).start();
    }

    public void setDefaultRating(int i) {
        this.defRating = i;
    }

    public void setRatingDialogListener(MoreEmojiDialogInterFace moreEmojiDialogInterFace) {
        this.mMoreEmojiDialogListener = moreEmojiDialogInterFace;
    }
}
