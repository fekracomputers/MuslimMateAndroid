package mindtrack.muslimorganizer.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.database.Database;
import mindtrack.muslimorganizer.model.Zeker;
import mindtrack.muslimorganizer.utility.NumbersLocal;

/**
 * Activity show azkar
 */
public class AzkarActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String TypeOfZeker;
    public ViewPager mViewPager;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azkar);
        context = this;
        Log.d("Azkar", getIntent().getStringExtra("title"));
        Log.d("Azkar", getIntent().getIntExtra("zekr_type", 1) + "");
        TypeOfZeker = getIntent().getStringExtra("title");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getIntent().getIntExtra("zekr_type", 1));
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    /**
     * Fragment of every zekr
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        private static final String CONTENT = "content",
                FADL = "fadl", REPEAT = "repeat",
                POSITION = "position", TYPE = "type";
        private View rootView;
        private WebView zekerContainer;
        private ImageView count, share;
        private TextView countDown;
        private ArrayList<String> sharedImageFiles = new ArrayList<>();
        private String content, fadl, type;

        /**
         * Share Button Click.
         * get hadith text and create bitmap with hadith text and share it.
         */
        private void onShareButtonClicked(String subject, String body) {
            // check if app grant write external storage permission.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // check permission for marshmellow.
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.
                    // Camera permission has not been granted yet. Request it directly.
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return;
                }
            }
            // create image from hadith and try share it
            Resources resources = getResources();
            // Create background bitmap
            Bitmap backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.backgroundtile);
            Bitmap.Config config = backgroundBitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }

            int width = 600 + (body.length() / 512) * 50;//backgroundBitmap.getWidth();

            // Create logo bitmap
            Bitmap logoBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
            logoBitmap = Bitmap.createScaledBitmap(logoBitmap, 128, 128, false);
            logoBitmap = logoBitmap.copy(config, false);
            int padding = 15;

            // Initiate text paint objects
            TextPaint titleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
            titleTextPaint.setStyle(Paint.Style.FILL);
            titleTextPaint.setTextSize(28);
            titleTextPaint.setColor(Color.rgb(64, 0, 0));
            titleTextPaint.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/simple.otf"));
            StaticLayout titleStaticLayout = new StaticLayout("منظم المسلم" + "\n" + subject, titleTextPaint, width - 3 * padding - logoBitmap.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.4f, 0.1f, false);
            TextPaint matnTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
            matnTextPaint.setStyle(Paint.Style.FILL);
            matnTextPaint.setTextSize(30);
            matnTextPaint.setColor(Color.BLACK);
            matnTextPaint.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/simple.otf"));
            StaticLayout matnStaticLayout = new StaticLayout(body + "\n", matnTextPaint, width - 2 * padding, Layout.Alignment.ALIGN_CENTER, 1.4f, 0.1f, false);
            int height = padding + Math.max(titleStaticLayout.getHeight(), logoBitmap.getHeight()) + padding + matnStaticLayout.getHeight() + padding;

            Bitmap bitmap = backgroundBitmap.copy(config, true);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            // create canvas and draw text on image.
            Canvas canvas = new Canvas(bitmap);
            canvas.save();
            tileBitmap(canvas, backgroundBitmap);
            canvas.drawBitmap(logoBitmap, width - padding - logoBitmap.getWidth(), padding, null);
            canvas.translate(padding, 2 * padding);
            titleStaticLayout.draw(canvas);
            canvas.translate(0, padding + logoBitmap.getHeight());
            matnStaticLayout.draw(canvas);
            canvas.restore();

            // share bitmap.
            shareImage(bitmap);
        }

        /**
         * Function to tile the bitmap in shared image
         *
         * @param canvas     canvas to draw in
         * @param tileBitmap bitmap to tile
         */
        private void tileBitmap(Canvas canvas, Bitmap tileBitmap) {
            int tileWidth = tileBitmap.getWidth();
            int tileHeight = tileBitmap.getHeight();
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            int y = 0;
            while (y < height) {
                int x = 0;
                while (x < width) {
                    canvas.drawBitmap(tileBitmap, x, y, null);
                    x += tileWidth;
                }
                y += tileHeight;
            }
        }


        /**
         * share hadith Bitmap.
         *
         * @param bitmap The new bitmap have hadith text info on it.
         */
        private void shareImage(Bitmap bitmap) {
            try {
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getActivity().getPackageName());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                final File file = File.createTempFile("sharedimage", ".png", directory);
                sharedImageFiles.add(file.getPath());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        sendIntent.setType("image/*");
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, " ");
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_via)));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                new Handler(getActivity().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }

        /**
         * Destroy fragment Object.
         * loop in shared images files and remove them from Device Storage.
         */
        @Override
        public void onDestroy() {
            for (String filePath : sharedImageFiles) {
                File file = new File(filePath);
                file.delete();
            }
            super.onDestroy();
        }

        public static PlaceholderFragment newInstance(String content, String fadl, int repeat, int position, String type) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(REPEAT, repeat);
            args.putString(FADL, fadl);
            args.putString(CONTENT, content);
            args.putInt(POSITION, (position + 1));
            args.putString(TYPE, type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_azkar_view, container, false);

            zekerContainer = (WebView) rootView.findViewById(R.id.webView);
            zekerContainer.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
            count = (ImageView) rootView.findViewById(R.id.time);
            share = (ImageView) rootView.findViewById(R.id.share);
            countDown = (TextView) rootView.findViewById(R.id.countDown);

            count.setOnClickListener(this);
            share.setOnClickListener(this);

            countDown.setText(NumbersLocal.convertNumberType(getContext() , String.valueOf(getArguments().getInt(REPEAT))));
            if (getArguments().getInt(REPEAT) == 1) {
                count.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                count.setImageResource(R.drawable.ic_move_next);
            }
            content = (getArguments().getString(CONTENT));
            fadl = (getArguments().getString(FADL)) == null ||
                    (getArguments().getString(FADL)).trim().equals("")
                    ? "الراوى غير متوفر" : (getArguments().getString(FADL)).trim();
            type = getArguments().getString(TYPE);

            if (Build.VERSION.SDK_INT >= 19) {
                // chromium, enable hardware acceleration
                zekerContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                // older android version, disable hardware acceleration
                zekerContainer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            zekerContainer.setBackgroundColor(Color.TRANSPARENT);

            zekerContainer.loadDataWithBaseURL("file:///android_asset/fonts/",
                    String.format("<head> <style>@font-face" +
                                    " {font-family: 'font' ;src: url('simple.otf');}div" +
                                    " { font-family: 'font';  word-spacing: 1px;} </style></head>" +
                                    "<body align='justify'" +
                                    " dir='rtl' style='line-height:1.4em ; font-size:x-large'> <div>" +
                                    " <span style='color:#3E686A'>%s</span> <br><font size='5.5'>%s</font> </div> </body>"
                            , fadl, content), "text/html", "utf8", "");
            return rootView;
        }

        @Override
        public void onClick(View view) {

            if (view == count || view == countDown) {
                //count down the number of repeats
                int repeat = Integer.parseInt(NumbersLocal.convertNumberTypeToEnglish(getContext() , countDown.getText().toString().trim()));
                if (repeat-- <= 0)
                    countDown.setText(NumbersLocal.convertNumberType(getContext() , String.valueOf(0)));
                else
                    countDown.setText(NumbersLocal.convertNumberType(getContext() , String.valueOf(repeat)));

                if (repeat == 0) {
                    ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
                    mViewPager.setCurrentItem(getArguments().getInt(POSITION), true);
                } else if (repeat == 1) {
                    count.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    count.setImageResource(R.drawable.ic_move_next);
                }

            } else if (view == share) {
                onShareButtonClicked(type, content + " " + fadl);
            }
        }
    }


    /**
     * Fragment adapter for azkar
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Zeker> zekerList;

        public SectionsPagerAdapter(FragmentManager fm, int type) {
            super(fm);
            zekerList = new Database().getAllAzkarOfType(type);
        }

        @Override
        public Fragment getItem(int position) {
            Zeker zeker = zekerList.get((zekerList.size() - 1) - position);
            return PlaceholderFragment.newInstance(zeker.content, zeker.fadl, zeker.numberOfRepeat, position, TypeOfZeker);
        }

        @Override
        public int getCount() {
            return zekerList.size();
        }

    }

}
