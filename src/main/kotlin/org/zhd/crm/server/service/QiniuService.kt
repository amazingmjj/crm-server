package org.zhd.crm.server.service

import com.qiniu.common.QiniuException
import com.qiniu.common.Zone
import com.qiniu.storage.BucketManager
import com.qiniu.storage.Configuration
import com.qiniu.storage.UploadManager
import com.qiniu.util.Auth
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File
import java.lang.Exception
import java.util.*

@Service
class QiniuService {

    object crmConfig {
        val accessKey = "mY7RLFWr1aV_Y-HYjz8NeYW1U9CvamR0OCQEU37Y"
        val secretKey = "lTnMbCZ7QpVI6pjafZnr7drGTeiO6YhK5RDPvahd"
        val buckeName = "zhdcrm"
        val outLink = "http://crm-cdn.xingyun361.com"
    }

    @Value("\${spring.profiles.active}")
    private var currentProfile = ""

    private val cfg = Configuration(Zone.zone1())

    private val uploadManager = UploadManager(cfg)

    fun getBucketManager(accessKey: String, secretKey: String): BucketManager {
        val auth = Auth.create(accessKey, secretKey)
        return BucketManager(auth, cfg)
    }

    fun getUploadToken(accessKey: String, secretKey: String, bucket: String) = Auth.create(accessKey, secretKey).uploadToken(bucket)

    fun getBucketFileList(manager: BucketManager, bucket: String, prefix: String = "", limit: Int = 1000, delimiter: String = ""): List<String>? {
        val it = manager.createFileListIterator(bucket, prefix, limit, delimiter)
        val list = it.next().asList().map { fi -> fi.key }
        return list
    }

    @Throws(QiniuException::class)
    fun uploadFile(imgUrl: String, file: File, uploadToken: String) {
        uploadManager.put(file, imgUrl, uploadToken)
    }

    @Throws(QiniuException::class)
    fun uploadFile(imgUrl: String, byteArray: ByteArray, uploadToken: String) {
        uploadManager.put(byteArray, imgUrl, uploadToken)
    }

    @Async
    @Throws(QiniuException::class)
    fun delFile(imgUrl: String, manager: BucketManager, bucket: String, baseUrl: String) {
        manager.delete(bucket, imgUrl.substring(baseUrl.length + 1))
    }

    @Throws(Exception::class)
    fun batchDeleteFiles(manager: BucketManager, bucket: String, delImgs: Array<String>) {
        val batchOperations = BucketManager.BatchOperations()
        batchOperations.addDeleteOp(bucket, *delImgs)
        manager.batch(batchOperations)
    }

    fun generatorFileName(folderName: String): String {
        return "$folderName/${currentProfile}_${randomSerial()}"
    }

    private fun randomSerial() = UUID.randomUUID().toString().replace("-", "").substring(0, 10)
}
