package com.zarkafit.zarka.service;

import com.zarkafit.zarka.entity.Usuario;
import com.zarkafit.zarka.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String senha = usuario.getSenha() != null ? usuario.getSenha() : "";
        String role = (usuario.getRole() == null || usuario.getRole().isBlank()) ? "ROLE_USER" : usuario.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new User(usuario.getEmail(), senha, List.of(new SimpleGrantedAuthority(role)));
    }
}
