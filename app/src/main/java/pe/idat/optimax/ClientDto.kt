package pe.idat.optimax

data class ClientDto(
                    var name: String,
                    var middleName: String,
                    var lastName: String,
                    var DNI: Int,
                    var phone: Int,
                    var email: String,
                    var ruc: String = "",
                    var codDistrict: DistrictResponse
                    )
