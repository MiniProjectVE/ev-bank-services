package id.co.bca.spring.evbankservices.entity.response;

public enum VEErrorCode {
    E00, E01, E02, E03, E04, E05, E06, E07, E08, E98, E99;

    public ErrorSchema getErrorSchema() {
        switch (this) {
            case E00:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Success", "Berhasil");
            case E01:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Failed", "Gagal");
            case E02:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Not Found", "Data Tidak Ditemukan");
            case E03:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Unavailable", "Tidak Tersedia");
            case E04:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Unauthorized", "Tidak Diijinkan");
            case E05:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Invalid", "Tidak Valid");
            case E06:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Inactive", "Tidak Aktif");
            case E07:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Unknown", "Tidak Diketahui");
            case E08:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Timeout", "Waktu Habis");
            case E98:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "Missing Configuration",
                        "Kesalahan Konfigurasi");
            case E99:
                return ErrorMessageBuilder.build(this.toString().replace("_", "-"), "System Exception",
                        "Kesalahan Pada Sistem");
            default:
                throw new AssertionError("Unknown Error Message" + this);
        }
    }

    public ErrorSchema getErrorSchema(String english, String indonesia) {
        return ErrorMessageBuilder.build(this.toString().replace("_", "-"), english, indonesia);
    }
}
