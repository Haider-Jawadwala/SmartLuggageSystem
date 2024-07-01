/*
 * PackList is an open-source packing-list for Android
 *
 * Copyright (c) 2017 Nicolas Bossard and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */



package com.nbossard.packlist.gui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ch.acra.acra.BuildConfig

import com.nbossard.packlist.R

//CHECKSTYLE : BEGIN GENERATED CODE
/*
@startuml
    class com.nbossard.packlist.gui.AboutActivity {
    }
    com.nbossard.packlist.gui.HelpThirdPartyActivity <.. com.nbossard.packlist.gui.AboutActivity : start through intent
@enduml
 */
//CHECKSTYLE : END GENERATED CODE

/**
 * About activity.
 *
 * @author nicolas Bossard
 */
class AboutActivity : AppCompatActivity() {

    // *********************** METHODS **********************************************************************

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // setting button listener

        // updating version number
        val mGeneralInfo = findViewById<TextView>(R.id.help__general_info__label)
        mGeneralInfo.text = String.format(getString(R.string.about__main),
                BuildConfig.VERSION_NAME,
                getString(R.string.about__main))

    }




    // *********************** PRIVATE METHODS ******************************************************************

    /**
     * Open the browser with project sources on GitHub.
     */
    private fun openBrowser() {
        val url = "https://github.com/nbossard/packlist"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }


    /**
     * Open the activity to display third party software licences.
     */

    companion object {

        // *********************** CONSTANTS**********************************************************************

        /** Log tag.  */
        private const val TAG = "AboutActivity"
    }
    //

}
