package com.example.myappartment.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class PostData(
    val postId: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val userImage: String? = null,
    val userContact: String? = null,
    val postImage: String? = null,
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val price: Int? = null,
    val rooms: Int? = null,
    val squareFootage: String? = null,
    val city: String? = null,
    val time: Long? = null,
    val searchTerms: List<String>? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(userImage)
        parcel.writeString(userContact)
        parcel.writeString(postImage)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(location)
        parcel.writeValue(price)
        parcel.writeValue(rooms)
        parcel.writeString(squareFootage)
        parcel.writeString(city)
        parcel.writeValue(time)
        parcel.writeStringList(searchTerms)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostData> {
        override fun createFromParcel(parcel: Parcel): PostData {
            return PostData(parcel)
        }

        override fun newArray(size: Int): Array<PostData?> {
            return arrayOfNulls(size)
        }
    }

    fun toMap() = mapOf(
        "postId" to postId,
        "userId" to userId,
        "username" to username,
        "userImage" to userImage,
        "userContact" to userContact,
        "postImage" to postImage,
        "title" to title,
        "description" to description,
        "location" to location,
        "price" to price,
        "rooms" to rooms,
        "squareFootage" to squareFootage,
        "city" to city,
        "time" to time,
        "searchTerms" to searchTerms
    )
}

