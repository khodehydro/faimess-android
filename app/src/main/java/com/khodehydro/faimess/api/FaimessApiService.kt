package com.khodehydro.faimess.api

import com.khodehydro.faimess.model.*
import retrofit2.Response
import retrofit2.http.*

/* ------------------------------------------------------------------ *
 *  FAIMESS Retrofit API Service — mirrors server/routes/*
 * ------------------------------------------------------------------ */

interface FaimessApiService {

    // ---- Auth ----
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun me(@Header("Authorization") token: String): Response<UserProfile>

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileRequest,
    ): Response<UserProfile>

    // ---- Tracks ----
    @GET("api/tracks")
    suspend fun listTracks(@Query("q") query: String? = null): Response<List<AudioTrack>>

    @GET("api/tracks/{id}")
    suspend fun getTrack(@Path("id") id: String): Response<AudioTrack>

    @GET("api/tracks/{id}/stream")
    suspend fun getStreamUrl(@Path("id") id: String): Response<StreamUrlResponse>

    // ---- Artists ----
    @GET("api/artists")
    suspend fun listArtists(): Response<List<Artist>>

    @GET("api/artists/{id}")
    suspend fun getArtist(@Path("id") id: String): Response<Artist>

    @POST("api/artists/{id}/follow")
    suspend fun toggleArtistFollow(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<FollowResponse>

    // ---- Albums ----
    @GET("api/albums")
    suspend fun listAlbums(): Response<List<Album>>

    @GET("api/albums/{id}")
    suspend fun getAlbum(@Path("id") id: String): Response<Album>

    @GET("api/albums/by-artist")
    suspend fun getAlbumsByArtist(@Query("artist") artist: String): Response<List<Album>>

    // ---- Playlists ----
    @GET("api/playlists")
    suspend fun listPlaylists(@Query("mine") mine: String? = null): Response<List<UserPlaylist>>

    @GET("api/playlists/{id}")
    suspend fun getPlaylist(@Path("id") id: String): Response<UserPlaylist>

    @POST("api/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") token: String,
        @Body body: CreatePlaylistRequest,
    ): Response<UserPlaylist>

    // ---- News ----
    @GET("api/news")
    suspend fun listNews(@Query("category") category: String? = null): Response<List<NewsItem>>

    @GET("api/news/{id}")
    suspend fun getNews(@Path("id") id: String): Response<NewsItem>

    @POST("api/news/{id}/comments")
    suspend fun addNewsComment(
        @Path("id") newsId: String,
        @Body body: AddNewsCommentRequest,
    ): Response<NewsComment>

    // ---- Shop ----
    @GET("api/shop")
    suspend fun listShopProducts(@Query("category") category: String? = null): Response<List<ShopProduct>>

    @GET("api/shop/{id}")
    suspend fun getShopProduct(@Path("id") id: String): Response<ShopProduct>

    @POST("api/shop/orders")
    suspend fun createOrder(@Body body: CreateOrderDto): Response<OrderReceipt>

    // ---- Comments (tracks / general) ----
    @GET("api/comments")
    suspend fun listComments(@Query("targetId") targetId: String): Response<List<TrackComment>>

    @POST("api/comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Body body: AddCommentRequest,
    ): Response<TrackComment>

    @POST("api/comments/{id}/report")
    suspend fun reportComment(
        @Header("Authorization") token: String,
        @Path("id") commentId: String,
        @Body body: ReportDto,
    ): Response<Unit>

    // ---- Social / Users ----
    @GET("api/users/{username}")
    suspend fun getUserProfile(@Path("username") username: String): Response<UserProfile>

    @GET("api/users/{username}/stats")
    suspend fun getFollowStats(@Path("username") username: String): Response<FollowStats>

    @POST("api/users/{username}/follow")
    suspend fun toggleFollow(
        @Header("Authorization") token: String,
        @Path("username") username: String,
    ): Response<FollowResponse>

    @GET("api/users/{username}/favorites")
    suspend fun getUserFavorites(@Path("username") username: String): Response<UserFavorites>

    // ---- Lyrics ----
    @GET("api/lyrics/{trackId}")
    suspend fun getLyrics(@Path("trackId") trackId: String): Response<List<LyricLine>>

    @POST("api/lyrics/submit")
    suspend fun submitLyrics(
        @Header("Authorization") token: String,
        @Body body: SubmitLyricsRequest,
    ): Response<LyricSubmission>

    // ---- Reports ----
    @POST("api/reports")
    suspend fun reportContent(
        @Header("Authorization") token: String,
        @Body body: ReportDto,
    ): Response<Unit>

    // ---- Badges & Points ----
    @GET("api/users/{username}/badges")
    suspend fun getUserBadges(@Path("username") username: String): Response<List<PlatformBadge>>

    @GET("api/users/{username}/points")
    suspend fun getUserPoints(@Path("username") username: String): Response<UserPointsResponse>

    // ---- Leaderboard ----
    @GET("api/leaderboard")
    suspend fun getLeaderboard(@Query("limit") limit: Int = 20): Response<List<LeaderboardEntry>>

    // ---- Banners ----
    @GET("api/banners")
    suspend fun listBanners(): Response<List<Banner>>
}

/* ---- Request / Response DTOs ---- */

data class RegisterRequest(
    val username: String,
    val password: String,
    val displayName: String? = null,
    val referralCode: String? = null,
)

data class LoginRequest(val username: String, val password: String)

data class AuthResponse(val token: String, val user: UserProfile)

data class UpdateProfileRequest(
    val displayName: String? = null,
    val bio: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val anthemTrackId: String? = null,
    val biasArtistId: String? = null,
)

data class StreamUrlResponse(val streamUrl: String, val expiresAt: Long)

data class FollowResponse(val following: Boolean)

data class UserFavorites(
    val trackIds: List<String>,
    val albumIds: List<String>,
    val playlistIds: List<String>,
)

data class CreatePlaylistRequest(
    val name: String,
    val trackIds: List<String> = emptyList(),
    val photo: String? = null,
    val mood: String? = null,
    val isDuo: Boolean = false,
)

data class AddCommentRequest(
    val targetId: String,
    val targetTitle: String,
    val text: String,
    val sourceType: String = "track",
)

data class AddNewsCommentRequest(
    val text: String,
    val authorName: String,
)

data class SubmitLyricsRequest(
    val trackId: String,
    val lines: List<LyricLine>,
    val submitterUsername: String,
    val submitterNotes: String? = null,
)

data class UserPointsResponse(
    val totalPoints: Int,
    val rules: List<PointRule>,
    val activity: FanActivity,
    val level: UserLevelInfo,
)