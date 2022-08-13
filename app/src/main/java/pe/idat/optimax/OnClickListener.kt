package pe.idat.optimax

interface OnClickListener {

    fun setTotal(total: Double)
    fun sumPrice(price: Double)
    fun subsTractPrice(price: Double)
    fun getQuantities(hashMap: HashMap<String,Int>)
}