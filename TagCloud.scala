package ttc

import java.awt.{ Color, Font, FontMetrics, Graphics2D }
import java.awt.geom.Area
import java.awt.image.BufferedImage


object TagCloud {

  val font = new Font("Helvetica", Font.BOLD, 72)
  val color = Color.BLUE

  def mostCommonWords(wordCount: Map[String, Int])(n: Int): List[String] =
    wordCount.toList.sortBy(_._2).map(_._1).reverse.take(n)

  def range(wordCount: Map[String, Int]): (Int, Int) =
    (wordCount.minBy(_._2)._2, wordCount.maxBy(_._2)._2)

  def tagCloud(words: List[String]): BufferedImage = {
    val cloud = tagCloudArea(words)
    val (w, h) = (
      cloud.getBounds2D().getWidth().toInt,
      cloud.getBounds2D().getHeight().toInt)

    withBufferedImage(w, h)(image => {
      withGraphics(image)(g2d => {
        g2d.setColor(color)
        g2d.draw(cloud)
        g2d.fill(cloud)
      })
      image
    })
  }

  def tagCloudArea(words: List[String]): Area =
    words.map(wordToArea).reduceLeft(addWord)

  def wordToArea(word: String): Area =
    withBufferedImage()(image =>
      withGraphics(image)(g2d => {
        val glyph = font.createGlyphVector(g2d.getFontRenderContext(), word)
        val lbounds = glyph.getLogicalBounds()
        val vbounds = glyph.getVisualBounds()

        new Area(glyph.getOutline(
          (lbounds.getX() - vbounds.getX()).toFloat,
          -vbounds.getY().toFloat))
      })
    )

  def withBufferedImage[A](width: Int = 1, height: Int = 1)(
      f: BufferedImage => A): A =
    f(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB))

  def withGraphics[A](image: BufferedImage)(f: Graphics2D => A): A = {
    val g2d = image.createGraphics()
    val ret = f(g2d)
    g2d.dispose()
    ret
  }

  def addWord = ???

}
