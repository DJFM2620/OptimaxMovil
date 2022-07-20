package pe.idat.optimax

import pe.idat.optimax.model.ArticleCartDto
import pe.idat.optimax.model.ArticleResponse

interface Communicator {

    fun passData(articleCartList: MutableList<ArticleCartDto>)
}