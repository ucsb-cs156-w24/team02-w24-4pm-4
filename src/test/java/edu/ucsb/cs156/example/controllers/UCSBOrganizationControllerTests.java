package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {

        @MockBean
        UCSBOrganizationRepository ucsbOrganizationRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsborganziations/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganization/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganization/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbOrganizationRepository.findById(eq("NSU"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=NSU"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("NSU"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganization with id NSU not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsborganizations() throws Exception {

                // arrange

                UCSBOrganization NSU = UCSBOrganization.builder()
                                .orgCode("NSU")
                                .orgTranslationShort("Nikkei Student Union")
                                .orgTranslation("Nikkei Student Union")
                                .inactive(false)
                                .build();

                UCSBOrganization thetaTau = UCSBOrganization.builder()
                                .orgCode("TT")
                                .orgTranslationShort("Theta Tau")
                                .orgTranslation("Theta Tau Engineering Fraternity")
                                .inactive(false)
                                .build();

                ArrayList<UCSBOrganization> expectedOrgs = new ArrayList<>();
                expectedOrgs.addAll(Arrays.asList(NSU, thetaTau));

                when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrgs);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrgs);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbdorganizations...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganization/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganization/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_organization() throws Exception {
                // arrange

                UCSBOrganization NSU = UCSBOrganization.builder()
                                .orgCode("NSU")
                                .orgTranslationShort("Nikkei Student Union")
                                .orgTranslation("Nikkei Student Union")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationRepository.save(eq(NSU))).thenReturn(NSU);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsborganization/post?orgCode=NSU&orgTranslationShort=Nikkei Student Union&orgTranslation=Nikkei Student Union&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).save(NSU);
                String expectedJson = mapper.writeValueAsString(NSU);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


        // Tests for GET /api/ucsbdiningcommons?...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsborganization?orgCode=NSU"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBOrganization NSU = UCSBOrganization.builder()
                                .orgCode("NSU")
                                .orgTranslationShort("Nikkei Student Union")
                                .orgTranslation("Nikkei Student Union")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("NSU"))).thenReturn(Optional.of(NSU));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=NSU"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("NSU"));
                String expectedJson = mapper.writeValueAsString(NSU);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for DELETE /api/ucsbdiningcommons?...

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_organization() throws Exception {
                // arrange

                UCSBOrganization NSU = UCSBOrganization.builder()
                                .orgCode("NSU")
                                .orgTranslationShort("Nikkei Student Union")
                                .orgTranslation("Nikkei Student Union")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("NSU"))).thenReturn(Optional.of(NSU));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganization?orgCode=NSU")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("NSU");
                verify(ucsbOrganizationRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id NSU deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_organization_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationRepository.findById(eq("FIERCE"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganization?orgCode=FIERCE")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("FIERCE");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id FIERCE not found", json.get("message"));
        }

        // Tests for PUT /api/ucsbdiningcommons?...

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_organization() throws Exception {
                // arrange

                UCSBOrganization NSUOrig = UCSBOrganization.builder()
                                .orgCode("NSU")
                                .orgTranslationShort("Nikkei Student Union")
                                .orgTranslation("Nikkei Student Union")
                                .inactive(false)
                                .build();

                UCSBOrganization NSUEdited = UCSBOrganization.builder()
                                .orgCode("NSUE")
                                .orgTranslationShort("UCSB Nikkei Student Union")
                                .orgTranslation("UCSB Nikkei Student Union")
                                .inactive(true)
                                .build();

                String requestBody = mapper.writeValueAsString(NSUEdited);

                when(ucsbOrganizationRepository.findById(eq("NSU"))).thenReturn(Optional.of(NSUOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganization?orgCode=NSU")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("NSU");
                verify(ucsbOrganizationRepository, times(1)).save(NSUEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_organization_that_does_not_exist() throws Exception {
                // arrange

                UCSBOrganization editedOrganization = UCSBOrganization.builder()
                                .orgCode("CSU")
                                .orgTranslationShort("Chinese Student Union")
                                .orgTranslation("UCSB Chinese Student Union")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(editedOrganization);

                when(ucsbOrganizationRepository.findById(eq("CSU"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganization?orgCode=CSU")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("CSU");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id CSU not found", json.get("message"));

        }
}
