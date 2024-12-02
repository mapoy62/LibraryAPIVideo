package com.oym.libraryapi.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.oym.libraryapi.R
import com.oym.libraryapi.databinding.FragmentLoginBinding
import com.oym.libraryapi.ui.message


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Propiedad para firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // Propiedades para el usuario y contraseña
    private var email = ""
    private var contrasenia = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        // Instanciamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Revisamos si ya tenemos a un usuario autenticado y lo pasamos al MainActivity
        if (firebaseAuth.currentUser != null) {
            actionLoginSuccessful()
        }

        // Botón para ingresar - Sign in
        binding.btnLogin.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            // Mostramos el progress bar
            binding.progressBar.visibility = View.VISIBLE

            // Autenticamos al usuario
            authenticateUser(email, contrasenia)
        }

        // Botón para registrarse - Sign up
        binding.btnRegistrarse.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            // Mostramos el progress bar
            binding.progressBar.visibility = View.VISIBLE

            firebaseAuth.createUserWithEmailAndPassword(email, contrasenia).addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    message("Usuario creado exitosamente")

                    // Enviamos un correo para verificar la dirección de email
                    firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        message("El correo de verificación ha sido enviado")
                    }?.addOnFailureListener {
                        message("No se pudo enviar el correo de verificación")
                    }

                    actionLoginSuccessful()
                } else {
                    binding.progressBar.visibility = View.GONE
                    handleErrors(authResult)
                }
            }
        }

        // Texto para recuperar contraseña
        binding.tvRestablecerPassword.setOnClickListener {
            val resetMail = EditText(requireContext())
            resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

            AlertDialog.Builder(requireContext())
                .setTitle("Restablecer contraseña")
                .setMessage("Ingrese su correo electrónico para recibir el enlace de restablecimiento")
                .setView(resetMail)
                .setPositiveButton("Enviar") { _, _ ->
                    val mail = resetMail.text.toString()
                    if (mail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                        // Mandamos el enlace de restablecimiento
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener {
                            message("El correo para restablecer la contraseña ha sido enviado")
                        }.addOnFailureListener {
                            message("El enlace no se ha podido enviar")
                        }
                    } else {
                        message("Favor de ingresar una dirección de correo válida")
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun validateFields(): Boolean {
        email = binding.tietEmail.text.toString().trim() // Elimina los espacios en blanco
        contrasenia = binding.tietContrasenia.text.toString().trim()

        // Verifica que el campo de correo no esté vacío
        if (email.isEmpty()) {
            binding.tietEmail.error = "Se requiere el correo"
            binding.tietEmail.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tietEmail.error = "El correo no tiene un formato válido"
            binding.tietEmail.requestFocus()
            return false
        }

        // Verifica que el campo de la contraseña no esté vacía y tenga al menos 6 caracteres
        if (contrasenia.isEmpty()) {
            binding.tietContrasenia.error = "Se requiere una contraseña"
            binding.tietContrasenia.requestFocus()
            return false
        } else if (contrasenia.length < 6) {
            binding.tietContrasenia.error = "La contraseña debe tener al menos 6 caracteres"
            binding.tietContrasenia.requestFocus()
            return false
        }
        return true
    }

    private fun handleErrors(task: Task<AuthResult>) {
        var errorCode = ""

        try {
            errorCode = (task.exception as FirebaseAuthException).errorCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (errorCode) {
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(requireContext(), "Error: El correo electrónico no tiene un formato correcto", Toast.LENGTH_SHORT).show()
                binding.tietEmail.error = "Error: El correo electrónico no tiene un formato correcto"
                binding.tietEmail.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                Toast.makeText(requireContext(), "Error: La contraseña no es válida", Toast.LENGTH_SHORT).show()
                binding.tietContrasenia.error = "La contraseña no es válida"
                binding.tietContrasenia.requestFocus()
                binding.tietContrasenia.setText("")
            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                Toast.makeText(requireContext(), "Error: Una cuenta ya existe con el mismo correo, pero con diferentes datos de ingreso", Toast.LENGTH_SHORT).show()
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(requireContext(), "Error: el correo electrónico ya está en uso con otra cuenta.", Toast.LENGTH_LONG).show()
                binding.tietEmail.error = ("Error: el correo electrónico ya está en uso con otra cuenta.")
                binding.tietEmail.requestFocus()
            }
            "ERROR_USER_TOKEN_EXPIRED" -> {
                Toast.makeText(requireContext(), "Error: La sesión ha expirado. Favor de ingresar nuevamente.", Toast.LENGTH_LONG).show()
            }
            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(requireContext(), "Error: No existe el usuario con la información proporcionada.", Toast.LENGTH_LONG).show()
            }
            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(requireContext(), "La contraseña proporcionada es inválida", Toast.LENGTH_LONG).show()
                binding.tietContrasenia.error = "La contraseña debe de tener por lo menos 6 caracteres"
                binding.tietContrasenia.requestFocus()
            }
            "NO_NETWORK" -> {
                Toast.makeText(requireContext(), "Red no disponible o se interrumpió la conexión", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(requireContext(), "Error. No se pudo autenticar exitosamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actionLoginSuccessful() {
        // Reemplaza el fragmento actual con el BooksListFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, BooksListFragment()) // Reemplazamos con el nuevo fragmento
        transaction.addToBackStack(null) // Opcional: agrega al backstack
        transaction.commit()
    }

    private fun authenticateUser(usr: String, psw: String) {
        firebaseAuth.signInWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->
            // Verificamos si fue exitosa la autenticación
            if (authResult.isSuccessful) {
                message("Autenticación exitosa")
                actionLoginSuccessful()
            } else {
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}