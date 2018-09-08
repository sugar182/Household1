package jp.ne.sugar182.household1.model

import android.content.Context
import android.util.Log
import jp.ne.sugar182.household1.repository.InnerStorage
import jp.ne.sugar182.household1.dto.PayData
import jp.ne.sugar182.household1.dto.PayDataMapper

// モデルとしてみたけど View（Activity）に情報を返すかは未定
// MVPモデルぽくなるのかな？
// ごりっとデータ操作を実装する。
// アプリケーションコンテキストにする？
class PayModel(context: Context) {

    // まず、１件のデータを作成
    // 全データのLIST化
    // 月毎のマップ　MAP(月,List)化

    val fileName = "HouseholdData.dat"
    val innnerStorage = InnerStorage(fileName, context)

    var maxIdx: Int = 0;
    val payDatas = arrayListOf<PayData>()
    val allMonthData = mutableMapOf<String, MutableList<PayData>>()

    init {
        //最初に全データを読み込む
        val datas = innnerStorage.readFile()

        for (data in datas) {
            Log.d("innnerStorageData", data)
            val payData = PayDataMapper().createPayData(data)
            val month = payData.payDate.substring(0, 7)
            Log.d("month", payData.payDate.substring(0, 7))

            // 月毎のリストにも登録（メモリー参照になってるといいな）
            setMonthData(payData)

            maxIdx++
        }
    }

    fun setMonthData(payData: PayData) {
        val month = payData.payDate.substring(0, 7)
        if (allMonthData.containsKey(month)) {
            allMonthData.get(month)!!.add(payData)
        } else {
            val payList: MutableList<PayData> = mutableListOf(payData)
            allMonthData.put(month, payList)
        }
    }


    //view層が利用するfunを実装 TODO Interfaceで実装
    fun addData(payData: PayData) {
        // インデックスを生成して書き込み
        payData.idx = getNewIndex()
        payDatas.add(payData)

        innnerStorage.saveFile(PayDataMapper().createJsonString(payData))
    }

    fun remove(payData: PayData) {
        payDatas.remove(payData)
    }

    // プライベートなfunを実装
    private fun getNewIndex(): Int {
        maxIdx++
        return maxIdx
    }


    // Null非許容に変えたい
    fun getMonthData(month : String): MutableList<PayData>? {
        return allMonthData.get(month)
    }

    //月の合計取得
    fun getTotal(month : String) : String{
        var sum : Long = 0
        val payDatas = getMonthData(month)
        if (payDatas != null) {
            for (payData in payDatas) {
                sum = sum + payData.pay
            }
        }
        return sum.toString()
    }


}