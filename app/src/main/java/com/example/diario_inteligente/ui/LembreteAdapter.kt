package com.example.diario_inteligente.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diario_inteligente.converter.Base64Converter
import com.example.diario_inteligente.databinding.ItemLembreteBinding
import com.example.diario_inteligente.model.Reminder

class LembreteAdapter(
    private var lista: List<Reminder>,
    private val onCliqueLembrete: (Reminder) -> Unit
) : RecyclerView.Adapter<LembreteAdapter.LembreteViewHolder>() {

    // Método simples para atualizar a lista quando o Firestore trouxer dados novos
    fun atualizarLista(novaLista: List<Reminder>) {
        this.lista = novaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LembreteViewHolder {
        val binding = ItemLembreteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LembreteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LembreteViewHolder, position: Int) {
        val lembrete = lista[position]
        holder.vincular(lembrete)
    }

    override fun getItemCount(): Int = lista.size

    inner class LembreteViewHolder(private val binding: ItemLembreteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun vincular(lembrete: Reminder) {

            binding.txtTituloItem.text = lembrete.title
            binding.txtDescricaoItem.text = lembrete.description
            binding.txtDataHoraItem.text =
                "${lembrete.date} às ${lembrete.time}"

            binding.txtPrazoItem.text = if (lembrete.prazoData.isNotEmpty() && lembrete.prazoHora.isNotEmpty()) {
                "Prazo: ${lembrete.prazoData} às ${lembrete.prazoHora}"
            } else {
                "Sem prazo definido"
            }

            if (lembrete.imageBase64.isNotEmpty()) {

                binding.imgReminder.setImageBitmap(
                    Base64Converter.stringToBitmap(
                        lembrete.imageBase64
                    )
                )
            }

            binding.root.setOnClickListener {
                onCliqueLembrete(lembrete)
            }
        }
    }
}