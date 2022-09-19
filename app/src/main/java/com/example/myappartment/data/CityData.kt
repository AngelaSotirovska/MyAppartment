package com.example.myappartment.data

import android.os.Parcel
import android.os.Parcelable

data class CityData(
    val cityId: String? = null,
    val name: String? = null,
    val longitude: Double? = null,
    val latitude: Double? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cityId)
        parcel.writeString(name)
        parcel.writeValue(longitude)
        parcel.writeValue(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CityData> {
        override fun createFromParcel(parcel: Parcel): CityData {
            return CityData(parcel)
        }

        override fun newArray(size: Int): Array<CityData?> {
            return arrayOfNulls(size)
        }
    }
}