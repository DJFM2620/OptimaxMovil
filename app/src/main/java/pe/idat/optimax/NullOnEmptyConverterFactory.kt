package pe.idat.optimax

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

class NullOnEmptyConverterFactory: Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<out kotlin.Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {

        val delegate: Converter<ResponseBody, *> = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
        return Converter { body ->
            if (body.contentLength() == 0L) null else delegate.convert(body)
        }
    }
 }