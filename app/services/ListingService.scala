package services

trait ListingService[T] {
  def list(limit:Int = 10, offset:Long = 0): List[T]
  def findById(id: Long): Option[T]
  def create(model: T): Option[Long]
  def delete(id: Long): Int
  def update(id: Long, model: T): Boolean
}
