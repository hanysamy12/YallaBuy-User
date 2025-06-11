package com.mariammuhammad.yallabuy.View.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.settings.ContactUs
import com.mariammuhammad.yallabuy.ViewModel.Settings.ContactUsViewModel


@Composable
fun ContactUsScreen(
    onNavigateBack: () -> Unit = {},
    contactViewModel: ContactUsViewModel = viewModel()
) {
    val contacts by contactViewModel.contacts.collectAsState()

    Scaffold(
        topBar = {
            ContactUsTopAppBar(onNavigateBack = onNavigateBack)
        },
        containerColor = Color.White
    ) { paddingValues ->
        ContactUsContent(paddingValues = paddingValues, contacts = contacts) {
            Image(
                painter = painterResource(id = R.drawable.contact_us_banner),
                contentDescription = "Contact Us Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsTopAppBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Contact Us", color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.dark_blue)
        )
    )
}

@Composable
fun ContactUsContent(
    paddingValues: PaddingValues,
    contacts: List<ContactUs>,
    imageContent: @Composable () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
           // Spacer(modifier = Modifier.height(16.dp))
            imageContent()
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "We are so happy to hear from you please feel free to contact us " +
                        "if you needed any assistance",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp),
                color = colorResource(R.color.dark_blue)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        items(contacts) { contact ->
            ContactCard(contact = contact)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ContactCard(contact: ContactUs) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 2.dp,
                color = colorResource(R.color.dark_blue),
                shape = RoundedCornerShape(8.dp)) ,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, contentDescription = "Name", tint = colorResource(R.color.dark_blue))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = contact.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Phone, contentDescription = "Phone", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = contact.phone, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Email, contentDescription = "Email", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = contact.email, fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true, name = "Contact Us Preview")
@Composable
fun ContactUsScreenPreview() {
    val previewContacts = listOf(
        ContactUs( "Hany Samy",
            "01226902530",
            "hanysamy111@outlook.com"),

        ContactUs(
            "Mariam Muhammad",
              "01123456789",
             "mariammuhammad911@gmail.com"),
            ContactUs(
            name = "Moaz Mamdouh",
            phone = "01095030319",
            email = "moaz.mamdoouh@gmail.com"
        ),
        ContactUs(
            name = "Ziad Elshemy",
            phone = "01067058501",
            email = "ziadmohamedelshemy@gmail.com")
    )
    Scaffold(
        topBar = { ContactUsTopAppBar(onNavigateBack = {}) },
        containerColor = Color.White
    ) { paddingValues ->
        ContactUsContent(paddingValues = paddingValues, contacts = previewContacts) {

            Image(
                painter = painterResource(id = R.drawable.contact_us_banner),
                contentDescription = "Contact Us Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}