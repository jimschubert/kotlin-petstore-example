package io.swagger.petstore.infrastructure

import okhttp3.*
import java.io.File

open class ApiClient(val baseUrl: String) {
    companion object {
        protected val ContentType = "Content-Type"
        protected val Accept = "Accept"
        protected val JsonMediaType = "application/json"

        @JvmStatic
        val client : OkHttpClient = OkHttpClient()

        @JvmStatic
        var defaultHeaders: Map<String, String> by ApplicationDelegates.setOnce(mapOf())

        @JvmStatic
        val jsonHeaders: Map<String, String> = mapOf(ContentType to JsonMediaType, Accept to JsonMediaType)
    }

    inline protected fun <reified T> requestBody(content: T, mediaType: String = JsonMediaType): RequestBody {
        if(content is File) {
            return RequestBody.create(
                    MediaType.parse(mediaType), content
            )
        } else if(mediaType == "application/json") {
            return RequestBody.create(
                    MediaType.parse(mediaType), Serializer.moshi.adapter(T::class.java).toJson(content)
            )
        }

        // TODO: this should be extended with other serializers
        TODO("requestBody currently only supports JSON body and File body.")
    }

    protected fun request(requestConfig: RequestConfig, body : Any? = null): Response {
        val httpUrl = HttpUrl.parse(baseUrl) ?: throw IllegalStateException("baseUrl is invalid.")

        var urlBuilder = httpUrl.newBuilder()
                .addPathSegments(requestConfig.path.trimStart('/'))

        requestConfig.query.forEach { k, v -> urlBuilder = urlBuilder.addQueryParameter(k,v) }

        val url = urlBuilder.build()
        val headers = requestConfig.headers + defaultHeaders

        var request : Request.Builder =  when (requestConfig.method) {
            RequestMethod.DELETE -> Request.Builder().url(url).delete()
            RequestMethod.GET -> Request.Builder().url(url)
            RequestMethod.HEAD -> Request.Builder().url(url).head()
            RequestMethod.PATCH -> Request.Builder().url(url).patch(requestBody(body!!, headers.getOrDefault(ContentType, JsonMediaType)))
            RequestMethod.PUT -> Request.Builder().url(url).put(requestBody(body!!, headers.getOrDefault(ContentType, JsonMediaType)))
            RequestMethod.POST -> Request.Builder().url(url).post(requestBody(body!!, headers.getOrDefault(ContentType, JsonMediaType)))
            RequestMethod.OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
        }

        headers.forEach { header -> request = request.addHeader(header.key, header.value) }

        val realRequest = request.build()
        val response = client.newCall(realRequest).execute()

        return response
    }

    protected inline fun <reified T : Any, reified K : Any?> jsonWithBody(requestConfig: RequestConfig, body : K? = null): ApiResponse<T?> {
        val response = request(requestConfig.copy(headers = requestConfig.headers + jsonHeaders), body)

        when {
            response.isRedirect -> return Redirection(
                    response.code(),
                    response.headers().toMultimap()
            )
            response.isInformational -> return Informational(
                    response.message(),
                    response.code(),
                    response.headers().toMultimap()
            )
            response.isSuccessful -> return Success(
                    Serializer.moshi.adapter(T::class.java).fromJson(response.body()?.string()),
                    response.code(),
                    response.headers().toMultimap()
            )
            response.isClientError -> return ClientError(
                    response.body()?.string(),
                    response.code(),
                    response.headers().toMultimap()
            )
            else -> return ServerError(
                    null,
                    response.body()?.string(),
                    response.code(),
                    response.headers().toMultimap()
            )
        }
    }

    protected inline fun <reified T : Any> json(requestConfig: RequestConfig): ApiResponse<T?> = jsonWithBody(requestConfig, null as? Any?)
}