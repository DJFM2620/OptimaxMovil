package pe.idat.optimax

import pe.idat.optimax.model.ArticleResponse

interface Communicator {

    fun passData(articleList: MutableList<ArticleResponse>)
}