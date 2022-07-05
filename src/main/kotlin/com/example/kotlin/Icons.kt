package com.example.kotlin

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon


object Icons {
  val FAVORITE_OUTLINE: Icon = load("/icons/favorite-outline.svg") // 16x16
  val FAVORITE_FILLED: Icon = load("/icons/favorite-filled.svg") // 16x16
  val CLOSE: Icon = load("/icons/close.svg") // 16x16
  val CLOSE_HOVERED: Icon = load("/icons/close-hovered.svg") // 16x16
  val EMPTY_ICON = EmptyIcon(16, 16)


  fun load(path: String): ImageIcon {
    val transcoder = Transcoder()
    val resource = Icons::class.java.getResource(path) ?: throw IllegalArgumentException()
    resource.openStream().use {
      transcoder.transcode(TranscoderInput(it), null)
    }
    return ImageIcon(transcoder.getImage())
  }

  private class Transcoder : ImageTranscoder() {
    private var image: BufferedImage? = null

    override fun createImage(w: Int, h: Int): BufferedImage {
      return BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB).also {
        image = it
      }
    }

    override fun writeImage(img: BufferedImage?, out: TranscoderOutput?) {}

    fun getImage(): BufferedImage = image ?: throw IllegalStateException()
  }
}

fun main() {
  val image = Icons.load("/icons/favorite-filled.svg")
}