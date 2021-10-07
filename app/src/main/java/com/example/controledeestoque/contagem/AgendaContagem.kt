package com.example.controledeestoque.contagem

import android.os.Parcel
import android.os.Parcelable

class AgendaContagem(val id:Int,
                     val categoria:String,
                     val qtdProduto:Int,
                     val data:String): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(categoria)
        parcel.writeInt(qtdProduto)
        parcel.writeString(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AgendaContagem> {
        override fun createFromParcel(parcel: Parcel): AgendaContagem {
            return AgendaContagem(parcel)
        }

        override fun newArray(size: Int): Array<AgendaContagem?> {
            return arrayOfNulls(size)
        }
    }
}