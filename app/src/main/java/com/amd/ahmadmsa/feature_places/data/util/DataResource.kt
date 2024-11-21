package com.amd.ahmadmsa.feature_places.data.util


import com.amd.ahmadmsa.feature_places.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


sealed class DataResource<T>(
    val data: T? = null,
    val code: Int? = null,
    val message: String? = null,
) {
    class Success<T>(data: T, code: Int?) : DataResource<T>(data, code)
    class Error<T>(message: String?, errorCode: Int?, data: T? = null) : DataResource<T>(data, errorCode, message)
    class Loading<T>(data: T? = null) : DataResource<T>(data)
}


fun <T, R> DataResource<T>.toDomain(mapper: (T) -> R): Resource<R> {
    return when (this) {
        is DataResource.Success -> Resource.Success(
            data = mapper(this.data!!),
            code = this.code
        )

        is DataResource.Error -> Resource.Error(
            message = this.message,
            errorCode = this.code,
            data = this.data?.let { mapper(it) }
        )

        is DataResource.Loading -> Resource.Loading(data = this.data?.let { mapper(it) })
    }
}


fun <T, R> Flow<DataResource<T>>.mapToDomain(mapper: (T) -> R): Flow<Resource<R>> {
    return this.map { dataResource ->
        when (dataResource) {
            is DataResource.Success -> Resource.Success(
                data = mapper(dataResource.data!!),
                code = dataResource.code
            )

            is DataResource.Error -> Resource.Error(
                message = dataResource.message,
                errorCode = dataResource.code,
                data = dataResource.data?.let { mapper(it) }
            )

            is DataResource.Loading -> Resource.Loading(
                data = dataResource.data?.let { mapper(it) }
            )
        }
    }
}

