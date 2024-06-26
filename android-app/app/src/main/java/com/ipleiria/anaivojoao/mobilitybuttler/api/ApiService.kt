package com.ipleiria.anaivojoao.mobilitybuttler.api

import com.google.gson.annotations.SerializedName
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.VoiceCommandEntity
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.applyParams
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import org.json.JSONObject
import retrofit2.http.POST


enum class ContentInfoEnum(val cnf: String){
	JSON("application/json"),
	CBOR("application/cbor"),
	XML("application/xml"),
	PLAINTEXT("text/plain"),
	EMPTY("");
	companion object {
		fun genericLogicResourceDecoder(cin: Cin?): String {
			return when(cin?.ContentInfo){
				ContentInfoEnum.JSON.cnf -> {
					var objson : JSONObject = JSONObject(cin.containerValue);
					//objson
					cin.containerValue?:""
				}
				ContentInfoEnum.PLAINTEXT.cnf -> cin.containerValue?.trim()?:""
				else -> cin?.containerValue?.trim()?:""
			}

		}
		fun checkContentInfo(info: String): ContentInfoEnum =
			ContentInfoEnum.values().firstOrNull { command ->
				command.cnf.firstOrNull { word -> info.trim().contains(word) } != null
			}?: ContentInfoEnum.EMPTY

	};
}
fun ContentInfoEnum.isIn(text: String): Boolean =
	cnf.sumOf { if (text.contains(it)) 1.toInt() else 0 } > 0


// Temperature {"m2m:cin": {"con": " 24.24\n", "cnf": "text/plain:0", "ri": "cin4633681802011007144", "pi": "cnt652548847908870411", "rn": "cin_478ndNX8vB", "ct": "20240604T173023,437997", "lt": "20240604T173023,437997", "ty": 4, "cs": 7, "st": 2, "et": "20290520T135918,198523"}}%
data class Cin(
	@SerializedName("con") val containerValue: String?=null,
	@SerializedName("cnf") val ContentInfo: String?= ContentInfoEnum.EMPTY.cnf
)
data class M2MResponse(
	@SerializedName("m2m:cin") val cin: Cin
)

interface ApiService {
	@GET("Mailbox/Mail/la")
	@Headers("X-M2M-Origin: CAdmin", "X-M2M-RI: 123","X-M2M-RVI: 3")
	fun checkMail(): Call<M2MResponse>
	@GET("Mailbox/Temperature/la")
	//TODO: this shouldn't be hardcoded and the origin, etc
	@Headers("X-M2M-Origin: CAdmin", "X-M2M-RI: 123","X-M2M-RVI: 3")
    fun getLatestTemperature(): Call<M2MResponse>
	@POST("butler/remember-list")
	@Headers("X-M2M-Origin: CAdminButler", "X-M2M-RI: 123","X-M2M-RVI: 3", "Content-Type: application/json;ty=4", "Accept: application/json")
	fun postRememberList(@Body payload: M2MResponse): Call<M2MResponse>
}