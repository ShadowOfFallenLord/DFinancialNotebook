package dark.andapp.dfinnb.presentaion.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dark.andapp.dfinnb.databinding.FragmentCreateTransactionBinding
import dark.andapp.dfinnb.domain.entity.NullableTransactionEntity
import dark.andapp.dfinnb.presentaion.extensions.dateToString
import dark.andapp.dfinnb.presentaion.extensions.launchWhenStarted
import dark.andapp.dfinnb.presentaion.extensions.toDomain
import dark.andapp.dfinnb.presentaion.viewmodels.BankAccountViewModel
import dark.andapp.dfinnb.presentaion.viewmodels.CategoryViewModel
import dark.andapp.dfinnb.presentaion.viewmodels.TransactionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CreateTransactionsFragment : Fragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionsViewModel: TransactionsViewModel by viewModels()
    private val bankAccountViewModel: BankAccountViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = this.requireContext()

        val newTransaction = NullableTransactionEntity()

        bankAccountViewModel.getAll().onEach {
            binding.sBank.adapter = ArrayAdapter(
                context,
                support_simple_spinner_dropdown_item,
                it.map { it.name }
            )

            binding.sBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    newTransaction.bank = it[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    newTransaction.bank = null
                }
            }
        }.launchWhenStarted(lifecycleScope)

        categoryViewModel.getAll().onEach {
            binding.sCategory.adapter = ArrayAdapter(
                context,
                support_simple_spinner_dropdown_item,
                it.map { it.name }
            )

            binding.sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    newTransaction.category = it[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    newTransaction.category = null
                }
            }
        }.launchWhenStarted(lifecycleScope)

        binding.cvCreatingDate.setOnClickListener {
            val todayCalendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    newTransaction.createdAt = calendar.time.time
                    binding.tvCreatingDate.text = calendar.time.dateToString("dd MMMM yyyy")
                },
                todayCalendar.get(Calendar.YEAR),
                todayCalendar.get(Calendar.MONTH),
                todayCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.cvAdd.setOnClickListener {
            var canCreate = true

            if (newTransaction.bank == null) {
                createDialog("Bank must be selected")
                canCreate = false
            }

            if (newTransaction.category == null) {
                createDialog("Category must be selected")
                canCreate = false
            }

            if (binding.etAmount.text.isEmpty()) {
                createDialog("Amount must be entered")
                canCreate = false
            } else {
                newTransaction.amount = binding.etAmount.text.toString().toDouble()
            }

            if (newTransaction.createdAt == null) {
                createDialog("Creating date must be entered")
                canCreate = false
            }

            if (canCreate) {
                newTransaction.comment = binding.etComment.text.toString()

                val domainTransaction = newTransaction.toDomain()
                createDialog(domainTransaction.toString())

                transactionsViewModel.createTransaction(domainTransaction)

                parentFragmentManager
                    .beginTransaction()
                    .remove(this)
                    .commit()
            }
        }

        binding.ivArrowBack.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        }
    }

    private fun createDialog(text: String) {
        val context = this.requireContext()
        AlertDialog.Builder(context)
            .setNeutralButton("Ok") { dialog, id -> dialog.cancel() }
            .setMessage(text)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}