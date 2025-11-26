package cl.duoc.milsabores.model.mappers

import cl.duoc.milsabores.data.remote.dto.ProductoDto
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.model.toUiModel

/**
 * Mapper de capa de datos (DTO) -> capa de UI (Producto).
 *
 * Si mañana cambia la forma en que mapeamos, solo tocamos este archivo.
 */
fun mapProductoDtoToUi(dto: ProductoDto): Producto =
    dto.toUiModel()
