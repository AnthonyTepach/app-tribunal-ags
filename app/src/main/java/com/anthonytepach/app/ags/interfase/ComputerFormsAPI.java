package com.anthonytepach.app.ags.interfase;

import com.anthonytepach.app.ags.model.ElementosSQL;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComputerFormsAPI {
    @GET("api/v1/ags/{alfanum}")
    Call<ElementosSQL> getFolio(@Path("alfanum") String alfanum);

}
