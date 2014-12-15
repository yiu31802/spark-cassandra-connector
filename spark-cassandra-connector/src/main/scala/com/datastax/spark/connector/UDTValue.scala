package com.datastax.spark.connector

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

import com.datastax.driver.core.{ProtocolVersion, UDTValue => DriverUDTValue}
import com.datastax.spark.connector.types.NullableTypeConverter

final class UDTValue(val fieldNames: IndexedSeq[String], val fieldValues: IndexedSeq[AnyRef])
  extends ScalaGettableData with Serializable {

}

object UDTValue {

  def fromJavaDriverUDTValue(value: DriverUDTValue)(implicit protocolVersion: ProtocolVersion): UDTValue = {
    val fields = value.getType.getFieldNames.toIndexedSeq
    val values = fields.map(AbstractGettableData.get(value, _))
    new UDTValue(fields, values)
  }

  val UDTValueTypeTag = implicitly[TypeTag[UDTValue]]

  implicit object UDTValueConverter extends NullableTypeConverter[UDTValue] {
    def targetTypeTag = UDTValueTypeTag
    def convertPF = {
      case x: UDTValue => x
    }
  }
}
