package com.khodehydro.faimess

import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.memory.MemoryCache
import coil.request.SuccessResult
import com.khodehydro.faimess.ui.components.offlinePhotoRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflinePhotoTest {
    @Test fun sameBundledPhotoIsReusedAcrossScreensWithoutDecodingAgain() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val loader = ImageLoader.Builder(context)
            .memoryCache { MemoryCache.Builder(context).maxSizeBytes(16 * 1024 * 1024).build() }
            .build()
        try {
            val first = loader.execute(offlinePhotoRequest(context, R.drawable.mountain_ridge))
            assertTrue(first is SuccessResult)
            val second = loader.execute(offlinePhotoRequest(context, R.drawable.mountain_ridge))
            assertTrue(second is SuccessResult)
            second as SuccessResult
            assertEquals(DataSource.MEMORY_CACHE, second.dataSource)
            assertSame(((first as SuccessResult).drawable as BitmapDrawable).bitmap, (second.drawable as BitmapDrawable).bitmap)
        } finally {
            loader.shutdown()
        }
    }
}
