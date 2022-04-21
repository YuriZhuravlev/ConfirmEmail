package confirm.email.data

sealed class Resource<T>(val status: Status, val data: T?, val error: Throwable?) {
    enum class Status {
        Loading, Success, Failed
    }

    class LoadingResource<T>(data: T? = null, error: Throwable? = null) : Resource<T>(Status.Loading, data, error)
    class SuccessResource<T>(data: T) : Resource<T>(Status.Success, data, null)
    class FailedResource<T>(error: Throwable, data: T? = null) : Resource<T>(Status.Failed, data, error)
}
