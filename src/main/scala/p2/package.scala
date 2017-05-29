import java.io.File

import scalismo.geometry._3D
import scalismo.image.DiscreteScalarImage
import scalismo.io.{ActiveShapeModelIO, ImageIO}
import scalismo.mesh.TriangleMesh
import scalismo.sampling.{DistributionEvaluator, ProposalGenerator}
import scalismo.sampling.loggers.AcceptRejectLogger
import scalismo.statisticalmodel.asm.{ActiveShapeModel, PreprocessedImage}

/**
  * MIT License
  *
  * Copyright (c) 2017 Giorgi Grigalashvili
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy 
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  **/
package object p2 {
  var countAccepted = 0
  var countRejected = 0
  val logger = new AcceptRejectLogger[ShapeParameters] {
    override def accept(current: ShapeParameters, sample: ShapeParameters, generator: ProposalGenerator[ShapeParameters], evaluator: DistributionEvaluator[ShapeParameters]): Unit = {
      println(s"Accepted proposal generated by $generator (probability ${evaluator.logValue(sample)})")
      countAccepted+=1
    }

    override def reject(current: ShapeParameters, sample: ShapeParameters, generator: ProposalGenerator[ShapeParameters], evaluator: DistributionEvaluator[ShapeParameters]): Unit = {
      println(s"Rejected proposal generated by $generator (probability ${evaluator.logValue(sample)})")
      countRejected+=1
    }
  }
  def likelihoodForMesh(asm: ActiveShapeModel, mesh: TriangleMesh, preprocessedImage: PreprocessedImage): Double = {

    val ids = asm.profiles.ids

    val likelihoods = for (id <- ids) yield {
      val profile = asm.profiles(id)
      val profilePointOnMesh = mesh.point(profile.pointId)
      val featureAtPoint = asm.featureExtractor(preprocessedImage, profilePointOnMesh, mesh, profile.pointId).get
      profile.distribution.logpdf(featureAtPoint)
    }
    likelihoods.sum
  }

}
