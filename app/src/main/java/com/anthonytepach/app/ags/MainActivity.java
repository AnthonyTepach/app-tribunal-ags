package com.anthonytepach.app.ags;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.anthonytepach.app.ags.conexion.RetrofitClient;
import com.anthonytepach.app.ags.interfase.ComputerFormsAPI;
import com.anthonytepach.app.ags.model.ElementosSQL;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private TextView textView_folio;
    private TextView textView_alfanum;
    private Button btn_validar;
    private Retrofit retrofit;
    private ImageView imageView;
    private EditText etAlfanum;
    private String folioOK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textView_folio = (TextView)findViewById(R.id.tv_folio);

        imageView = (ImageView)findViewById(R.id.imageView5);
        int fol=Integer.parseInt(getIntent().getStringExtra("folio"));
        String a= String.format("%06d",fol);
        etAlfanum = findViewById(R.id.et_alfanum);
        btn_validar = findViewById(R.id.btn_validar);
        folioOK=a;

        btn_validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etAlfanum.getText().toString().isEmpty()){
                    if (etAlfanum.getText().toString().length()==6){
                        getAlfanum(folioOK,etAlfanum.getText().toString());
                    }else{
                        Toast.makeText(MainActivity.this, "El alfanúmerico debe tener una longitud de 6 caracteres", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    void getAlfanum(String folio,String alfanum){
        String api_url="https://intelligentforms.mx/TribunalAGS/";
        retrofit = RetrofitClient.getClient(api_url);

        ComputerFormsAPI computerFormsAPI = retrofit.create(ComputerFormsAPI.class);
        Call<ElementosSQL> call= computerFormsAPI.getFolio(folio);
        call.enqueue(new Callback<ElementosSQL>() {
            @Override
            public void onResponse(Call<ElementosSQL> call, Response<ElementosSQL> response) {
                //
                if (response.isSuccessful()){
                    if (response.code()==200){
                        ElementosSQL elementosSQL= response.body();
                        String alfa = elementosSQL.getAlfanum();
                        if (!alfa.equals(alfanum)){
                            hideSoftKeyboard();
                            Toast.makeText(MainActivity.this, "Debes respetar las mayúsculas", Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.ic_no_valido);
                        }else{
                            hideSoftKeyboard();
                            loadImageGlide(Uri.parse("https://computerforms.com.mx/tsj_ags/"+alfa+".png"));
                            Toast.makeText(MainActivity.this, alfa+".png", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else{
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<ElementosSQL> call, Throwable t) {
                hideSoftKeyboard();
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.ic_no_valido);
                t.printStackTrace();
            }
        });


    }

    private void loadImageGlide(Uri photoUrl){
        RequestOptions options = new RequestOptions()
                .autoClone()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error_descarga)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(this).load(photoUrl)
                .apply(options)
                .into(imageView);

    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}