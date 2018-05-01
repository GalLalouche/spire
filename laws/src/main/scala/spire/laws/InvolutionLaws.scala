package spire.laws

import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws
import spire.algebra._
import spire.syntax.eq._
import spire.syntax.vectorSpace._
import spire.syntax.ring._
import spire.syntax.involution._
import InvalidTestException._

object InvolutionLaws {
  def apply[A: Eq: Arbitrary] = new InvolutionLaws[A] {
    def Equ: Eq[A] = Eq[A]
    def Arb: Arbitrary[A] = implicitly[Arbitrary[A]]
  }
}

trait InvolutionLaws[A] extends Laws {

  implicit def Equ: Eq[A]
  implicit def Arb: Arbitrary[A]

  def involution(implicit A: Involution[A]) = new DefaultRuleSet(
    name = "involution",
    parent = None,
    "involution" -> forAllSafe( (x: A) => x.adjoint.adjoint === x )
  )

  def involutionMultiplicativeSemigroup(implicit A: Involution[A], mm: MultiplicativeSemigroup[A]) =  new DefaultRuleSet(
    name = "involutionMultiplicativeSemigroup",
    parent = Some(involution),
    "antiautomorphism" -> forAllSafe( (x: A, y: A) => (x * y).adjoint === y.adjoint * x.adjoint)
  )

  def involutionMultiplicativeMonoid(implicit A: Involution[A], mm: MultiplicativeMonoid[A]) =  new DefaultRuleSet(
    name = "involutionMultiplicativeMonoid",
    parent = Some(involutionMultiplicativeSemigroup),
    "preserves one" -> (mm.one.adjoint === mm.one)
  )

  def involutionRing(implicit A: Involution[A], ringA: Ring[A]) = new DefaultRuleSet(
    name = "involutionRing",
    parent = Some(involutionMultiplicativeMonoid),
    "compatible with addition" -> forAllSafe( (x: A, y: A) => (x + y).adjoint === x.adjoint + y.adjoint)
  )

  def involutionAlgebra[R:Arbitrary](implicit A: Involution[A], R: Involution[R], algebra: FieldAssociativeAlgebra[A, R]) = new DefaultRuleSet(
    name = "involutionAlgebra",
    parent = Some(involutionRing),
    "conjugate linear" -> forAllSafe( (x: A, y: A, r: R) => ( r *: x + y ) === (r.adjoint *: x.adjoint + y.adjoint) )
  )

}
