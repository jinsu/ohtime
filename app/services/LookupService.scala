package services

trait LookupService[T] {
  def list(limit:Int = 10, offset:Int = 0): List[T]
  def findByAttr(model: T): Option[T]
  def create(model: T): Option[Long]
  def delete(model: T): Int
  def update(curModel: T, newModel: T): Boolean
}
